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
import bgu.dcr.az.mas.impl.ds.FastSingletonMap;
import bgu.dcr.az.mas.misc.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
    private final Map<Integer, LinkedList<AgentWithManipulator>> controlledAgents;
    private final Set<Integer> finishedAgents;
    private final Queue<AZIPMessage>[] messageQueue;
    private final Queue<AZIPMessage> delayedMessageQueue;
    protected final Execution execution;
    protected final Logger logger;
    private Agent activeAgent;

    private int tick;
    private boolean giveupBeforeComplete = true;

    public BaseAgentController(int id, Execution<?> ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id);
        logger = ex.require(Logger.class);

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
            initializeAgent(agent, manipulator, aId, ex);
            AgentWithManipulator awm = new AgentWithManipulator(agent, manipulator);
            LinkedList<AgentWithManipulator> agentStack = controlledAgents.get(aId);
            if (agentStack == null) {
                agentStack = new LinkedList<>();
                controlledAgents.put(aId, agentStack);
            }
            agentStack.add(awm);
        }

        router.register(this, controlled);
        this.tick = 0;
    }

    public void setGiveupBeforeComplete(boolean giveupBeforeComplete) {
        this.giveupBeforeComplete = giveupBeforeComplete;
    }

    protected Map<Integer, LinkedList<AgentWithManipulator>> getControlledAgents() {
        return controlledAgents;
    }

    @Override
    protected void start() {
        for (LinkedList<AgentWithManipulator> a : controlledAgents.values()) {
            activeAgent = a.getLast().a;
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
                AgentWithManipulator a = controlledAgents.get(m.getAgentRecepient()).getLast();
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
                    removeControlledAgent(a);
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
        controlledAgents.remove(a.a.getId());
        finishedAgents.add(a.a.getId());
        if (controlledAgents.isEmpty()) {
            terminate();
        }
    }

    @Override
    public void send(Message m, int recepientAgent) {
        Context senderContext = controlledAgents.get(m.getSender()).getLast().getCurrentContext();

        if (controlledAgents.containsKey(recepientAgent)) {
            final AZIPMessage aMessage = new AZIPMessage(m.copy(), pid(), recepientAgent, senderContext);
            if (senderContext == controlledAgents.get(recepientAgent).getLast().getCurrentContext()) {
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
        if (message.getContext() == controlledAgents.get(message.getData().getRecepient()).getLast().getCurrentContext()) {
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

//    public ContinuationMediator nest(Agent agent) {
//        LinkedList<AgentWithManipulator> agentStack = controlledAgents.get(agent.getId());
//        if (agentStack == null) {
//            agentStack = new LinkedList<>();
//            controlledAgents.put(agent.getId(), agentStack);
//        }
//        agentStack.add(null)
//    }

    protected abstract void initializeAgent(Agent agent, AgentManipulator manipulator, int aId, Execution ex);

    protected static class AgentWithManipulator {

        public Agent a;
        public AgentManipulator am;
        private Context currentContext;

        public AgentWithManipulator(Agent a, AgentManipulator am) {
            this.a = a;
            this.am = am;
        }

        public Context getCurrentContext() {
            return currentContext;
        }

        public void setCurrentContext(Context context) {
            currentContext = context;
        }

    }

}
