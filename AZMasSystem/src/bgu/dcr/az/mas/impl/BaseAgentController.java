/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.algo.AgentManipulator;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.execs.AbstractProc;
import bgu.dcr.az.mas.AZIPMessage;
import bgu.dcr.az.mas.AgentController;
import bgu.dcr.az.mas.AgentDistributer;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.MessageRouter;
import bgu.dcr.az.mas.impl.Context.ContextGenerator;
import bgu.dcr.az.mas.impl.ds.FastSingletonMap;
import bgu.dcr.az.mas.misc.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 *
 * @author User
 */
public abstract class BaseAgentController extends AbstractProc implements AgentController {

    private final MessageRouter router;
    private final int numAgents;
    private Map<Integer, AgentStateStack> controlledAgents;
    private final Set<Integer> finishedAgents;
    private final Mailbox mailbox;
    protected final Execution<?> execution;
    protected final Logger logger;
    private final ContextGenerator cGen;
    private AgentState activeAgent;

    private int tick;
    private boolean giveupBeforeComplete = true;
    private final AgentDistributer distributor;
    private final AgentSpawner spawner;

    public BaseAgentController(int id, Execution<?> ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id);
        this.execution = ex;
        logger = execution.require(Logger.class);
        this.cGen = execution.require(ContextGenerator.class);
        this.router = execution.require(MessageRouter.class);

        this.mailbox = new Mailbox();

        distributor = ex.require(AgentDistributer.class);
        spawner = execution.require(AgentSpawner.class);
        int[] controlled = distributor.getControlledAgentsIds(id);
        
        if (controlled.length == 1) {
            controlledAgents = new FastSingletonMap<>();
        } else {
            controlledAgents = new HashMap<>();
        }

        this.numAgents = distributor.getNumberOfAgents();
        finishedAgents = new HashSet<>();
        router.register(this, controlled);
        this.tick = 0;

    }

    public void setGiveupBeforeComplete(boolean giveupBeforeComplete) {
        this.giveupBeforeComplete = giveupBeforeComplete;
    }

    protected Map<Integer, AgentStateStack> getControlledAgents() {
        return controlledAgents;
    }

    @Override
    protected void start() {
        int[] controlled = distributor.getControlledAgentsIds(pid());
        
        for (int aId : controlled) {
            try {
                AgentManipulator manipulator = RegisteryUtils.getRegistery().getAgentManipulator(spawner.getAgentType(aId));
                Agent agent = manipulator.create();
                nest(agent, aId, null);
            } catch (Exception ex) {
                Agt0DSL.panic("cannot start agent " + aId + ", see cause.", ex);
            }
        }

    }

    @Override
    protected final void onIdleDetected() {
        switch (execution.getEnvironment()) {
            case async:
                handleIdle();
                break;
            case sync:
                beforeNextTick();
                tick++;
                break;
            default:
                throw new AssertionError(execution.getEnvironment().name());

        }

        if (!mailbox.isEmpty()) {
//            System.out.println("Agent " + pid() + " found message in its queue");
            wakeup(pid());
        } else {
            sleep();
        }
    }

    protected abstract void handleIdle();

    protected abstract void beforeNextTick();

    @Override
    protected void quota() {
        while (true) {
            Message m = mailbox.getMessage();

            if (m != null) {
                activeAgent = controlledAgents.get(m.getRecepient()).current();
                if (activeAgent == null) {
                    if (!finishedAgents.contains(m.getRecepient())) {
                        Agt0DSL.panic("AgentController: " + pid() + " got unexpected message for agent: " + m.getRecepient());
                    }
                    return;
                }
                
                Message newM = activeAgent.a.setCurrentMessage(m);
                if (newM != null) {
                    activeAgent.am.callHandler(activeAgent.a, newM.getName(), newM.getArgs());
                }

                if (activeAgent.a.isFinished()) {
                    activeAgent.finilize();
                    activeAgent = null;
                    return;
                }
                activeAgent = null;

                if (mailbox.isEmpty()) {
                    sleep();
                    return;
                }
            } else {
                sleep();
                return;
            }

            //Half eager implementation
            if (giveupBeforeComplete && !mailbox.isEmpty() && ThreadLocalRandom.current().nextBoolean()) {
                return;
            }
        }
    }

    protected void removeControlledAgent(AgentState a) {
        removeControlledAgent(a.a);
    }

    protected void removeControlledAgent(Agent a) {
        controlledAgents.remove(a.getId());
        finishedAgents.add(a.getId());
        if (controlledAgents.isEmpty()) {
            terminate();
        }
    }

    @Override
    public void send(Message m, int recepientAgent) {
        Context senderContext = activeAgent.getContext();

        if (controlledAgents.containsKey(recepientAgent)) {
            mailbox.deliverMessage(new AZIPMessage(m.copy(), pid(), recepientAgent, senderContext));
        } else {
            router.route(m, recepientAgent, senderContext);
        }
    }

    @Override
    public void receive(AZIPMessage message) {
        mailbox.deliverMessage(message);
        if (execution.getEnvironment() == ExecutionEnvironment.async) {
            wakeup(pid());
        }
    }

    @Override
    public int getTickNumber() {
        return tick;
    }

    @Override
    public void log(int agentId, String msg) {
        logger.log("Agent " + agentId, msg);
    }

    protected AgentState getActiveAgent() {
        return activeAgent;
    }

    @Override
    public int getControllerId() {
        return pid();
    }

    /**
     * Replaces the active agent with a given one, saves the last agent
     * configuration to stack. Tries to automatically generate the current
     * context (if contextId is null) by generating current stack of nested
     * calls or (if contextId is not null) uses it as context id.
     *
     * @param agent
     * @param contextId
     * @return
     * @throws ClassNotFoundException
     */
    public ContinuationMediator nest(Agent agent, String contextId) throws ClassNotFoundException {
        return nest(agent, activeAgent.a.getId(), contextId);
    }

    private ContinuationMediator nest(Agent agent, int aId, String contextId) throws ClassNotFoundException {
        AgentStateStack agentStack = controlledAgents.get(aId);
        if (agentStack == null) {
            agentStack = new AgentStateStack(aId);
            controlledAgents.put(aId, agentStack);
        }

        if (contextId == null) {
            contextId = agentStack.isEmpty()
                    ? agent.getClass().getSimpleName()
                    : agentStack.current().getContext().getContextRepresentation() + "_" + agent.getClass().getSimpleName();
        }

        return agentStack.delayedNest(agent, contextId);
    }

    protected abstract void initializeAgent(Agent agent, AgentManipulator manipulator, int aId, Execution ex);

    protected static class AgentState {

        public Agent a;
        public AgentManipulator am;
        private final Context context;
        private final ContinuationMediator continuation;

        public AgentState(Agent a, AgentManipulator am, Context context, ContinuationMediator continuation) {
            this.a = a;
            this.am = am;
            this.context = context;
            this.continuation = continuation;
        }

        public Context getContext() {
            return context;
        }

        public ContinuationMediator getContinuationMediator() {
            return continuation;
        }

        public void finilize() {
            continuation.executeContinuation();
        }
    }

    protected class AgentStateStack {

        private final int aId;
        private final ConcurrentLinkedDeque<AgentState> stack;

        public AgentStateStack(int aId) {
            stack = new ConcurrentLinkedDeque<>();
            this.aId = aId;
        }

        public int getaId() {
            return aId;
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public AgentState current() {
            return stack.peek();
        }

        public ContinuationMediator delayedNest(Agent agent, String contextId) throws ClassNotFoundException {
            ContinuationMediator mediator = new ContinuationMediator() {
                @Override
                public void executeContinuation() {
                    restore(true);
                    super.executeContinuation();
                }
            };

            AgentManipulator manipulator = RegisteryUtils.getRegistery().getAgentManipulator(agent.getClass());
            initializeAgent(agent, manipulator, aId, execution);

//            if (isEmpty()) {
//                System.out.println("Spawned " + agent + " with context " + contextId);
//            } else {
//                System.out.println("Request to nest new Agent for algorithm " + agent.getClass().getSimpleName() + " inside " + activeAgent.a + " with context " + current().getContext().getContextRepresentation() + " with context " + contextId);
//            }

            Context context = cGen.getContext(contextId);
            AgentState nestedAgent = new AgentState(agent, manipulator, context, mediator);
            stack.addFirst(nestedAgent);
            AgentState old = activeAgent;
            activeAgent = nestedAgent;
            restore(false);
            nestedAgent.a.start();
            activeAgent = old;

            return mediator;
        }

        private void restore(boolean shouldPoll) {
            if (shouldPoll) {
                AgentState lastAgent = stack.poll();
                BaseAgentController.this.activeAgent = null;

                if (stack.isEmpty()) {
                    removeControlledAgent(lastAgent.a);
                } else {
                    activeAgent = current();
//                    System.out.println("Nested " + lastAgent.a + " has finished running, and swapped with " + activeAgent);
                }
            }

            if (activeAgent != null) {
                mailbox.restoreContextMessages();
            }
        }
    }

    private class Mailbox {

        private boolean restoreContextMessages;
        private final Queue<AZIPMessage>[] messageQueue;
        private Queue<AZIPMessage> delayedMessageQueue;
        private Queue<AZIPMessage> temporalMessageQueue;

        public Mailbox() {
            switch (execution.getEnvironment()) {
                case async:
                    this.messageQueue = new Queue[]{new ConcurrentLinkedQueue()};
                    break;
                case sync:
                    this.messageQueue = new Queue[]{new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()};
                    break;
                default:
                    throw new AssertionError(execution.getEnvironment().name());
            }

            delayedMessageQueue = new LinkedList<>();
            temporalMessageQueue = new LinkedList<>();
            restoreContextMessages = false;
        }

        private Queue<AZIPMessage> currentMessageQueue() {
            return messageQueue[tick % messageQueue.length];
        }

        private Queue<AZIPMessage> nextMessageQueue() {
            return messageQueue[(tick + 1) % messageQueue.length];
        }

        public boolean isEmpty() {
            return currentMessageQueue().isEmpty();
        }

        public void deliverMessage(AZIPMessage message) {
            nextMessageQueue().add(message);
        }

        private void delayMessage(AZIPMessage message) {
            if (restoreContextMessages) {
                temporalMessageQueue.add(message);
            } else {
                delayedMessageQueue.add(message);
            }
        }

        public void restoreContextMessages() {
            restoreContextMessages = true;
        }

        public Message getMessage() {
            while (true) {
                AZIPMessage m;

                if (!restoreContextMessages) {
                    m = currentMessageQueue().poll();
                } else {
                    if (delayedMessageQueue.isEmpty()) {
                        Queue<AZIPMessage> temp = delayedMessageQueue;
                        delayedMessageQueue = temporalMessageQueue;
                        restoreContextMessages = false;
                        temporalMessageQueue = temp;
                        continue;
                    }
                    
                    m = delayedMessageQueue.poll();
                }

                if (m == null) {
                    return null;
                }

                AgentStateStack agentStack = controlledAgents.get(m.getAgentRecepient());
                AgentState awm = null;

                if (agentStack == null || (awm = agentStack.current()) == null || m.getContext() != awm.getContext()) {
                    delayMessage(m);
                    continue;
                }

                return m.getData();
            }
        }
    }
}
