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
import bgu.dcr.az.api.Message;
import bgu.dcr.az.execs.AbstractProc;
import bgu.dcr.az.mas.AZIPMessage;
import bgu.dcr.az.mas.AgentController;
import bgu.dcr.az.mas.AgentDistributer;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.MessageRouter;
import bgu.dcr.az.mas.impl.ds.FastSingletonMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public abstract class BaseAgentController extends AbstractProc implements AgentController {

    private final MessageRouter router;
    private int numAgents;
    private final Map<Integer, AgentWithManipulator> controlledAgents;
    private final Queue<AZIPMessage> messageQueue;

    private int tick;

    public BaseAgentController(int id, Execution ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id);
        this.router = ex.require(MessageRouter.class);
        this.messageQueue = router.getMessageQueue(id);

        AgentDistributer distributor = ex.require(AgentDistributer.class);
        AgentSpawner spawner = ex.require(AgentSpawner.class);
        int[] controlled = distributor.getControlledAgentsIds(id);

        this.numAgents = distributor.getNumberOfAgents();

        if (controlled.length == 1) {
            controlledAgents = new FastSingletonMap<>();
        } else {
            controlledAgents = new HashMap<>();
        }

        for (int aId : controlled) {
            AgentManipulator manipulator = RegisteryUtils.getDefaultRegistery().getAgentManipulator(spawner.getAgentType(aId));
            Agent agent = manipulator.create();
            initializeAgent(agent, manipulator, aId);
            AgentWithManipulator awm = new AgentWithManipulator(agent, manipulator);
            controlledAgents.put(aId, awm);
        }
    }

    @Override
    protected void start() {
        for (AgentWithManipulator a : controlledAgents.values()) {
            a.a.start();
        }
    }

    @Override
    protected void quota() {

        AZIPMessage m = messageQueue.poll();

        if (m != null) {
            AgentWithManipulator a = controlledAgents.get(m.getAgentRecepient());
            Message newM = a.a.setCurrentMessage(m.getData());
            if (newM != null) {
                a.am.callHandler(a.a, newM.getName(), newM.getArgs());
            }

            if (a.a.isFinished()) {
                controlledAgents.remove(a.a.getId());

                if (controlledAgents.isEmpty()) {
                    terminate();
                    return;
                }
            }
            
            if (messageQueue.isEmpty()) {
                sleep();
            }

        } else {
            sleep();
        }
    }

    @Override
    public void send(Message m, int recepientAgent) {
        if (controlledAgents.containsKey(recepientAgent)) {
            messageQueue.add(new AZIPMessage(m.copy(), pid(), recepientAgent));
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
        messageQueue.add(message);
    }

    @Override
    public int getControllerId() {
        return pid();
    }

    protected abstract void initializeAgent(Agent agent, AgentManipulator manipulator, int aId);

    private static class AgentWithManipulator {

        Agent a;
        AgentManipulator am;

        public AgentWithManipulator(Agent a, AgentManipulator am) {
            this.a = a;
            this.am = am;
        }

    }

}
