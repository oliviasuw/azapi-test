/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.algo.AgentManipulator;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.mas.AZIPMessage;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.impl.BaseAgentController;
import bgu.dcr.az.mas.impl.InitializationException;
import bgu.dcr.az.mas.stat.data.MessageReceivedInfo;
import bgu.dcr.az.mas.stat.data.MessageSentInfo;
import java.util.LinkedList;

/**
 *
 * @author User
 */
public class CPAgentController extends BaseAgentController {

    private final CPExecution exec;

    public CPAgentController(int id, CPExecution ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id, ex);

        this.exec = ex;
    }

    public Problem getGlobalProblem() {
        return exec.data().getProblem();
    }

    public void report(String who, Agent a, Object[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void assign(int id, int value) {
        exec.data().getSolution().assign(id, value);
    }

    public void unassign(int id) {
        exec.data().getSolution().unassign(id);
    }

    public Integer getAssignment(int id) {
        Integer result = exec.data().getSolution().assignmentOf(id);
        if (result == null) {
            Agt0DSL.panic("attempting to get assignment when no such exists ( agent: " + id + ")");
        }

        return result;
    }

    public void assignAll(Assignment a) {
        exec.data().getSolution().assignAll(a);
    }

    @Override
    protected void initializeAgent(Agent agent, AgentManipulator manipulator, int aId, Execution ex) {
        Agent.PlatformOperationsExtractor.extract(agent).initialize(aId, this, ex);
    }

    public void reportNoSolution() {
        exec.data().getSolution().setStateNoSolution();
    }

    @Override
    protected void handleIdle() {
        LinkedList<AgentState> killedAgents = new LinkedList<>();
        for (AgentStateStack a : getControlledAgents().values()) {
            AgentState agent = a.current();
            agent.a.onIdleDetected();
            if (agent.a.isFinished()) {
                killedAgents.add(agent);
            }
        }

        killedAgents.forEach(this::removeControlledAgent);
    }

    @Override
    protected void beforeNextTick() {
        LinkedList<AgentState> killedAgents = new LinkedList<>();
        for (AgentStateStack a : getControlledAgents().values()) {
            final AgentState agent = a.current();
            agent.a.onMailBoxEmpty();
            if (agent.a.isFinished()) {
                killedAgents.add(agent);
            }
        }

        killedAgents.forEach(this::removeControlledAgent);
    }

    @Override
    public void send(Message m, int recepientAgent) {

        if (exec.informationStream().hasListeners(MessageSentInfo.class)) {
            exec.informationStream().write(new MessageSentInfo(m.getMessageId(), m.getSender(), recepientAgent, m.getName(), exec.data().getCcCount()[m.getSender()]));
        }

        super.send(m, recepientAgent);
    }

    @Override
    public void receive(AZIPMessage message) {
        if (exec.informationStream().hasListeners(MessageReceivedInfo.class)) {
            exec.informationStream().write(new MessageReceivedInfo(message.getData().getMessageId(), message.getData().getSender(), message.getData().getRecepient(), message.getData().getName()));
        }

        super.receive(message);
    }

}
