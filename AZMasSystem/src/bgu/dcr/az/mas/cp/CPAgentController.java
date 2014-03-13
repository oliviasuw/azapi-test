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

    private final CPExecution execution;

    public CPAgentController(int id, CPExecution ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id, ex);

        this.execution = ex;
    }

    public Problem getGlobalProblem() {
        return execution.data().getProblem();
    }

    public void report(String who, Agent a, Object[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void assign(int id, int value) {
        execution.data().getSolution().assign(id, value);
    }

    public void unassign(int id) {
        execution.data().getSolution().unassign(id);
    }

    public Integer getAssignment(int id) {
        Integer result = execution.data().getSolution().assignmentOf(id);
        if (result == null) {
            Agt0DSL.panic("attempting to get assignment when no such exists ( agent: " + id + ")");
        }

        return result;
    }

    public void assignAll(Assignment a) {
        execution.data().getSolution().assignAll(a);
    }

    @Override
    protected void initializeAgent(Agent agent, AgentManipulator manipulator, int aId, Execution ex) {
        Agent.PlatformOperationsExtractor.extract(agent).initialize(aId, this, ex);
    }

    public void reportNoSolution() {
        execution.data().getSolution().setStateNoSolution();
    }

    @Override
    protected void handleIdle() {
        LinkedList<AgentWithManipulator> killedAgents = new LinkedList<>();
        for (AgentContextStack a : getControlledAgents().values()) {
            AgentWithManipulator agent = a.current();
            agent.a.onIdleDetected();
            if (agent.a.isFinished()) {
                killedAgents.add(agent);
            }
        }

        killedAgents.forEach(this::removeControlledAgent);
    }

    @Override
    protected void beforeNextTick() {
        LinkedList<AgentWithManipulator> killedAgents = new LinkedList<>();
        for (AgentContextStack a : getControlledAgents().values()) {
            final AgentWithManipulator agent = a.current();
            agent.a.onMailBoxEmpty();
            if (agent.a.isFinished()) {
                killedAgents.add(agent);
            }
        }

        killedAgents.forEach(this::removeControlledAgent);
    }

    @Override
    public void send(Message m, int recepientAgent) {

        if (execution.informationStream().hasListeners(MessageSentInfo.class)) {
            execution.informationStream().write(new MessageSentInfo(m.getMessageId(), m.getSender(), recepientAgent, m.getName(), execution.data().getCcCount()[m.getSender()]));
        }

        super.send(m, recepientAgent);
    }

    @Override
    public void receive(AZIPMessage message) {
        if (execution.informationStream().hasListeners(MessageReceivedInfo.class)) {
            execution.informationStream().write(new MessageReceivedInfo(message.getData().getMessageId(), message.getData().getSender(), message.getData().getRecepient(), message.getData().getName()));
        }

        super.receive(message);
    }

}
