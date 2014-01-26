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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public abstract class BaseAgentController extends AbstractProc implements AgentController {

    private final MessageRouter router;
    private int numAgents;
    private final Map<Integer, AgentWithManipulator> controlledAgents;
    private final Set<Integer> finishedAgents;
    private final Queue<AZIPMessage>[] messageQueue;
    protected final Execution execution;

    private int tick;

    public BaseAgentController(int id, Execution ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id);
        this.execution = ex;
        this.router = ex.require(MessageRouter.class);

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
            AgentManipulator manipulator = RegisteryUtils.getDefaultRegistery().getAgentManipulator(spawner.getAgentType(aId));
            Agent agent = manipulator.create();
            initializeAgent(agent, manipulator, aId);
            AgentWithManipulator awm = new AgentWithManipulator(agent, manipulator);
            controlledAgents.put(aId, awm);
        }

        router.register(this, controlled);
        this.tick = 0;
    }

    protected Map<Integer, AgentWithManipulator> getControlledAgents() {
        return controlledAgents;
    }

    @Override
    protected void start() {
        for (AgentWithManipulator a : controlledAgents.values()) {
            a.a.start();
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

        if (!currentMessageQueue().isEmpty()) {
//            System.out.println("Agent " + pid() + " found message in its queue");
            wakeup(pid());
        }else {
            sleep();
        }
    }

    protected abstract void handleIdle();

    protected abstract void beforeNextTick();

    @Override
    protected void quota() {
        final Queue<AZIPMessage> mq = currentMessageQueue();

        AZIPMessage m = mq.poll();

        if (m != null) {
            AgentWithManipulator a = controlledAgents.get(m.getAgentRecepient());
            if (a == null) {
                if (!finishedAgents.contains(m.getAgentRecepient())) {
                    Agt0DSL.panic("AgentController: " + pid() + " got unexpected message for agent: " + m.getAgentRecepient());
                }
                return;
            }
            Message newM = a.a.setCurrentMessage(m.getData());
            if (newM != null) {
                a.am.callHandler(a.a, newM.getName(), newM.getArgs());
            }

            if (a.a.isFinished()) {

                removeControlledAgent(a);
                return;
            }

            if (mq.isEmpty()) {
                sleep();
            }

        } else {
            sleep();
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
        if (controlledAgents.containsKey(recepientAgent)) {
            nextMessageQueue().add(new AZIPMessage(m.copy(), pid(), recepientAgent));
        } else {
            router.route(m, recepientAgent);
        }
    }

    @Override
    public void broadcast(Message m) {
        for (int i = 0; i < numAgents; i++) {
            send(m, i);
        }
    }

    @Override
    public int getTickNumber() {
        return tick;
    }

    @Override
    public void log(int agentId, String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receive(AZIPMessage message) {
        nextMessageQueue().add(message);
        if (execution.getEnvironment() == ExecutionEnvironment.async) {
            wakeup(pid());
        }
    }

    @Override
    public int getControllerId() {
        return pid();
    }

    protected abstract void initializeAgent(Agent agent, AgentManipulator manipulator, int aId);

    protected static class AgentWithManipulator {

        public Agent a;
        public AgentManipulator am;

        public AgentWithManipulator(Agent a, AgentManipulator am) {
            this.a = a;
            this.am = am;
        }

    }

}
