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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author User
 */
public abstract class BaseAgentController extends AbstractProc implements AgentController {

    private final MessageRouter router;
    private final int numAgents;
    private final Map<Integer, AgentContextStack> controlledAgents;
    private final Set<Integer> finishedAgents;
    private final Queue<AZIPMessage>[] messageQueue;
    private final Queue<AZIPMessage> delayedMessageQueue;
    protected final Execution execution;
    protected final Logger logger;
    private final ContextGenerator cGen;
    private Agent activeAgent;

    private int tick;
    private boolean giveupBeforeComplete = true;

    public BaseAgentController(int id, Execution<?> ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id);
        logger = ex.require(Logger.class);
        this.cGen = ex.require(ContextGenerator.class);
        this.execution = ex;
        this.router = ex.require(MessageRouter.class);
        this.delayedMessageQueue = new ConcurrentLinkedQueue();

        switch (ex.getEnvironment()) {
            case async:
                this.messageQueue = new Queue[]{new ConcurrentLinkedQueue()};
                break;
            case sync:
                this.messageQueue = new Queue[]{new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()};
                break;
            default:
                throw new AssertionError(ex.getEnvironment().name());

        }

        AgentDistributer distributor = ex.require(AgentDistributer.class);
        AgentSpawner spawner = ex.require(AgentSpawner.class);
        int[] controlled = distributor.getControlledAgentsIds(id);

        this.numAgents = distributor.getNumberOfAgents();

        if (controlled.length == 1) {
            controlledAgents = new FastSingletonMap<>();
        } else {
            controlledAgents = new HashMap<>();
        }

        finishedAgents = new HashSet<>();

        for (int aId : controlled) {
            AgentManipulator manipulator = RegisteryUtils.getRegistery().getAgentManipulator(spawner.getAgentType(aId));
            Agent agent = manipulator.create();
            nest(agent, aId, null);
        }

        router.register(this, controlled);
        this.tick = 0;
    }

    public void setGiveupBeforeComplete(boolean giveupBeforeComplete) {
        this.giveupBeforeComplete = giveupBeforeComplete;
    }

    protected Map<Integer, AgentContextStack> getControlledAgents() {
        return controlledAgents;
    }

    @Override
    protected void start() {
        for (AgentContextStack a : controlledAgents.values()) {
            activeAgent = a.current().a;
            activeAgent.start();
        }
        activeAgent = null;
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

        if (!currentMessageQueue().isEmpty()) {
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
        final Queue<AZIPMessage> mq = currentMessageQueue();

        while (true) {
            AZIPMessage m = mq.poll();

            if (m != null) {
                AgentWithManipulator a = controlledAgents.get(m.getAgentRecepient()).current();
                if (a == null) {
                    if (!finishedAgents.contains(m.getAgentRecepient())) {
                        Agt0DSL.panic("AgentController: " + pid() + " got unexpected message for agent: " + m.getAgentRecepient());
                    }
                    return;
                }
                activeAgent = a.a;
                Message newM = activeAgent.setCurrentMessage(m.getData());
                if (newM != null) {
                    a.am.callHandler(activeAgent, newM.getName(), newM.getArgs());
                }

                if (activeAgent.isFinished()) {
                    controlledAgents.get(activeAgent.getId()).current().finilize();
                    activeAgent = null;
                    return;
                }
                activeAgent = null;

                if (mq.isEmpty()) {
                    sleep();
                    return;
                }

            } else {
                sleep();
                return;
            }

            //Half eager implementation
            if (giveupBeforeComplete && !mq.isEmpty() && ThreadLocalRandom.current().nextBoolean()) {
                return;
            }
        }
    }

    private Queue<AZIPMessage> currentMessageQueue() {
        return messageQueue[tick % messageQueue.length];
    }

    private Queue<AZIPMessage> nextMessageQueue() {
        return messageQueue[(tick + 1) % messageQueue.length];
    }

    protected void removeControlledAgent(AgentWithManipulator a) {
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
        Context senderContext = controlledAgents.get(m.getSender()).current().getContext();

        if (controlledAgents.containsKey(recepientAgent)) {
            final AZIPMessage aMessage = new AZIPMessage(m.copy(), pid(), recepientAgent, senderContext);
            if (senderContext == controlledAgents.get(recepientAgent).current().getContext()) {
                nextMessageQueue().add(aMessage);
            } else {
                delayedMessageQueue.add(aMessage);
            }
        } else {
            router.route(m, recepientAgent, senderContext);
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

    @Override
    public void receive(AZIPMessage message) {
        AgentContextStack agentStack = controlledAgents.get(message.getData().getRecepient());
        AgentWithManipulator awm = null;

        if (agentStack == null || (awm = agentStack.current()) == null) {
            return;
        }

        if (message.getContext() == awm.getContext()) {
            nextMessageQueue().add(message);
            if (execution.getEnvironment() == ExecutionEnvironment.async) {
                wakeup(pid());
            }
        } else {
            delayedMessageQueue.add(message);
        }
    }

    protected Agent getActiveAgent() {
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
        return nest(agent, activeAgent.getId(), contextId);
    }

    private ContinuationMediator nest(Agent agent, int aId, String contextId) throws ClassNotFoundException {
        AgentContextStack agentStack = controlledAgents.get(aId);
        if (agentStack == null) {
            agentStack = new AgentContextStack();
            controlledAgents.put(aId, agentStack);
        }
        AgentContextStack fAgentStack = agentStack;
        ContinuationMediator mediator = new ContinuationMediator() {
            @Override
            public void executeContinuation() {
                fAgentStack.pop();
                activeAgent = null;

                if (fAgentStack.isEmpty()) {
                    removeControlledAgent(agent);
                } else {
                    activeAgent = fAgentStack.current().a;
                }

                if (activeAgent != null) {
                    for (int i = 0; i < delayedMessageQueue.size(); i++) {
                        AZIPMessage m = delayedMessageQueue.poll();
                        if (m == null) {
                            break;
                        }
                        receive(m);
                    }
                }

                super.executeContinuation();
            }
        };
        AgentManipulator manipulator = RegisteryUtils.getRegistery().getAgentManipulator(agent.getClass());
        initializeAgent(agent, manipulator, aId, execution);
        if (contextId == null) {
            contextId = agentStack.isEmpty()
                    ? manipulator.getAlgorithmName()
                    : agentStack.current().getContext().getContextRepresentation() + "_" + manipulator.getAlgorithmName();
        }
        Context context = cGen.getContext(contextId);
        AgentWithManipulator awm = new AgentWithManipulator(agent, manipulator, context, mediator);
        agentStack.push(awm);
        activeAgent = agent;

        return mediator;
    }

    protected abstract void initializeAgent(Agent agent, AgentManipulator manipulator, int aId, Execution ex);

    protected static class AgentWithManipulator {

        public Agent a;
        public AgentManipulator am;
        private final Context context;
        private final ContinuationMediator continuation;

        public AgentWithManipulator(Agent a, AgentManipulator am, Context context, ContinuationMediator continuation) {
            this.a = a;
            this.am = am;
            this.context = context;
            this.continuation = continuation;
        }

        public Context getContext() {
            return context;
        }

        public void finilize() {
            continuation.executeContinuation();
        }
    }

    protected static class AgentContextStack {

        private final ConcurrentLinkedQueue<AgentWithManipulator> stack;

        public AgentContextStack() {
            stack = new ConcurrentLinkedQueue<>();
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public AgentWithManipulator current() {
            return stack.peek();
        }

        public void push(AgentWithManipulator awm) {
            stack.add(awm);
        }

        public AgentWithManipulator pop() {
            return stack.poll();
        }
    }
}
