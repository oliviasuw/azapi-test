/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim;

import bgu.dcr.az.common.collections.FastSingletonMap;
import bgu.dcr.az.execs.exps.exe.ExecutionEnvironment;
import static bgu.dcr.az.execs.exps.exe.ExecutionEnvironment.async;
import static bgu.dcr.az.execs.exps.exe.ExecutionEnvironment.sync;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.SimulationConfiguration;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.lowlevel.AbstractProc;
import bgu.dcr.az.execs.sim.nest.Continuation;
import bgu.dcr.az.execs.sim.nest.ContinuationMediator;
import bgu.dcr.az.execs.sim.net.AZIPMessage;
import bgu.dcr.az.execs.sim.net.Message;
import bgu.dcr.az.execs.sim.net.MessageRouter;
import bgu.dcr.az.execs.statistics.info.MessageInfo;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author User
 */
public class SimulatedMachine extends AbstractProc {

    private MessageRouter router;
    private Mailbox mailbox;

    private Map<Integer, AgentStateStack> controlledAgents;
    private Set<Integer> finishedAgents;

    protected Simulation simulation;
    private ContextGenerator cGen;
    protected AgentState activeAgent;

    private ExecutionEnvironment env;
    private int tick;
    private InfoStream infos;

    public SimulatedMachine(int id, Simulation sim) {
        super(id);
        initialize(sim);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    private void initialize(Simulation mc) {
        simulation = mc;
        infos = mc.infoStream();
        final SimulationConfiguration configuration = mc.configuration();
        env = configuration.env();

        if (!mc.isInstalled(ContextGenerator.class)) {
            mc.install(ContextGenerator.class, new ContextGenerator());
        }

        cGen = mc.require(ContextGenerator.class);
        this.mailbox = new Mailbox();
        this.router = mc.require(MessageRouter.class);

        int[] controlled = configuration.agentsInMachine(pid());

        if (controlled.length == 1) {
            controlledAgents = new FastSingletonMap<>();
        } else {
            controlledAgents = new HashMap<>();
        }

        for (int ca : controlled) {
            controlledAgents.put(ca, new AgentStateStack(ca));
        }

        finishedAgents = new HashSet<>();
        router.register(this, controlled);
        this.tick = 0;
    }

    protected Map<Integer, AgentStateStack> getControlledAgents() {
        return controlledAgents;
    }

    private void setActiveAgent(AgentState active) {
        if (active == null) {
            unsetActiveAgent();
        } else {
            activeAgent = active;
            Agent.currentAgent.set(active.a.getAgent());
        }
    }

    private void unsetActiveAgent() {
        activeAgent = null;
        Agent.currentAgent.set(null);
    }

    @Override
    protected void start() {
        for (Integer aId : controlledAgents.keySet()) {
            try {
                Agent agent = createAgent(aId);
                nest(agent, aId, null).andWhenDoneDo(null);
            } catch (Exception ex) {
                Agt0DSL.panic("Cannot start agent " + aId + ", see cause.", ex);
            }
        }
    }

    public boolean isControlling(int aid) {
        return controlledAgents.keySet().contains(aid);
    }

    @Override
    protected final void onIdleDetected() {
        switch (env) {
            case async:
                handleIdle();
                break;
            case sync:
                tick++;
                handleNextTick();
                break;
            default:
                throw new AssertionError(env.name());

        }

        if (!mailbox.isEmpty()) {
            wakeup(pid());
        } else {
            sleep();
        }
    }

    protected void handleIdle() {
        for (AgentStateStack a : getControlledAgents().values().toArray(new AgentStateStack[getControlledAgents().size()])) {
            setActiveAgent(a.current());
            activeAgent.a.handleIdle();
            if (activeAgent.a.isFinished()) {
                activeAgent.finilize();
            }
        }
        unsetActiveAgent();
    }

    protected void handleNextTick() {
        for (AgentStateStack a : getControlledAgents().values()) {
            setActiveAgent(a.current());
            activeAgent.a.handleTick(tick);
            if (activeAgent.a.isFinished()) {
                activeAgent.finilize();
            }
        }

        unsetActiveAgent();
    }

    @Override
    protected void quota() {
        while (true) {
            Message m = mailbox.getMessage();

            if (m != null) {
                setActiveAgent(controlledAgents.get(m.getRecepient()).current());
                if (activeAgent == null) {
                    if (!finishedAgents.contains(m.getRecepient())) {
                        Agt0DSL.panic("AgentController: " + pid() + " got unexpected message for agent: " + m.getRecepient());
                    }
                    return;
                }

                activeAgent.a.handleMessage(m);
                if (activeAgent.a.isFinished()) {
                    activeAgent.finilize();
                    unsetActiveAgent();
                    return;
                }
                unsetActiveAgent();

                if (mailbox.isEmpty()) {
                    sleep();
                    return;
                }
            } else {
                sleep();
                return;
            }

            //Half eager implementation
            if (env == async && !mailbox.isEmpty() && ThreadLocalRandom.current().nextBoolean()) {
                return;
            }
        }
    }

    protected void removeControlledAgent(AgentState a) {
        removeControlledAgent(a.a);
    }

    protected void removeControlledAgent(Agent.Internals a) {
        controlledAgents.remove(a.getId());
        finishedAgents.add(a.getId());
        if (controlledAgents.isEmpty()) {
            terminate();
        }
    }

    public void send(Message m, int recepientAgent) {
        AgentContext senderContext = activeAgent.getContext();

        if (controlledAgents.containsKey(recepientAgent)) {
            mailbox.deliverMessage(new AZIPMessage(m.copy(), pid(), recepientAgent, senderContext));
        } else {
            infos.writeIfListening(() -> new MessageInfo(m.getMessageId(), m.getSender(), m.getRecepient(), m.getName(), MessageInfo.OperationType.Sent), MessageInfo.class);
            router.route(m, recepientAgent, senderContext);
        }
    }

    public void receive(AZIPMessage message) {

        infos.writeIfListening(() -> {
            Message m = message.getData();
            return new MessageInfo(m.getMessageId(), m.getSender(), m.getRecepient(), m.getName(), MessageInfo.OperationType.Received);
        }, MessageInfo.class);

        mailbox.deliverMessage(message);
        if (env == ExecutionEnvironment.async) {
            wakeup(pid());
        }
    }

    public int getTickNumber() {
        return tick;
    }

    protected AgentState getActiveAgent() {
        return activeAgent;
    }

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

    private Agent createAgent(Integer aId) {
        Class<? extends Agent> aclass = simulation.configuration().agentClass(aId);
        Agent result = ConstructorAccess.get(aclass).newInstance();
//        Agent.internalsOf(result).initialize(aId, this, result, simulation.configuration().agentInitializationArgs(aId));
        return result;
    }

    protected static class AgentState {

        public Agent.Internals a;
        private final AgentContext context;
        private final ContinuationMediator continuation;

        public AgentState(Agent.Internals a, AgentContext context, ContinuationMediator continuation) {
            this.a = a;
            this.context = context;
            this.continuation = continuation;
        }

        public AgentContext getContext() {
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
            return new ContinuationMediator() {

                @Override
                public void executeContinuation() {
                    restore(true);
                    super.executeContinuation();
                }

                @Override
                public void andWhenDoneDo(Continuation c) {
                    super.andWhenDoneDo(c);

                    Agent.Internals internals = Agent.internalsOf(agent);
                    internals.initialize(aId, SimulatedMachine.this, agent, simulation.configuration().agentInitializationArgs(aId));
                    AgentContext context = cGen.getContext(contextId);
                    AgentState nestedAgent = new AgentState(internals, context, this);
                    stack.addFirst(nestedAgent);
                    AgentState old = activeAgent;
                    setActiveAgent(nestedAgent);
                    restore(false);

                    internals.start();
                    if (internals.isFinished()) {
                        nestedAgent.finilize();
                    }
                    setActiveAgent(old);
                }

            };

        }

        private void restore(boolean shouldPoll) {
            if (shouldPoll) {
                AgentState lastAgent = stack.poll();
                SimulatedMachine.this.unsetActiveAgent();

                if (stack.isEmpty()) {
                    removeControlledAgent(lastAgent.a);
                } else {
                    setActiveAgent(current());
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
            switch (env) {
                case async:
                    this.messageQueue = new Queue[]{new ConcurrentLinkedQueue()};
                    break;
                case sync:
                    this.messageQueue = new Queue[]{new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()};
                    break;
                default:
                    throw new AssertionError(env.name());
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

    public static class ContextGenerator implements Module {

        private ConcurrentHashMap<String, AgentContext> contextMapper;
        private long contextId;

        public ContextGenerator() {

            contextMapper = new ConcurrentHashMap<>();
            contextId = 0;
        }

        public AgentContext getContext(String repr) {
            AgentContext context = contextMapper.get(repr);

            if (context == null) {
                context = new AgentContext(repr, contextId++);

                AgentContext old = contextMapper.putIfAbsent(repr, context);
                if (old != null) {
                    context = old;
                }
            }

            return context;
        }
    }
}
