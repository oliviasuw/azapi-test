/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.dcr.execution.manipulators.AgentManipulator;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.dcr.Agt0DSL;
import bgu.dcr.az.dcr.api.Agent;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.Message;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.execution.statistics.ExternalMessageReceivedInfo;
import bgu.dcr.az.dcr.execution.statistics.ExternalMessageSentInfo;
import bgu.dcr.az.dcr.execution.statistics.InternalMessageReceivedInfo;
import bgu.dcr.az.dcr.execution.statistics.InternalMessageSentInfo;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 */
public class CPAgentController extends AgentController {

    private final CPExecution exec;

    public CPAgentController(int id, CPExecution ex) throws ClassNotFoundException, ConfigurationException, InitializationException {
        super(id, ex);

        this.exec = ex;
    }

    public Execution<?> getExecution() {
        return execution;
    }

    public Problem getGlobalProblem() {
        return exec.data().getProblem();
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

    public void reportFinalCost(int cost) {
        exec.data().getSolution().setFinalCost(cost);
    }

    public void reportNoSolution() {
        exec.data().getSolution().setStateNoSolution();
    }

    @Override
    protected void handleIdle() {
        for (AgentStateStack a : getControlledAgents().values().toArray(new AgentStateStack[getControlledAgents().size()])) {
            activeAgent = a.current();
            activeAgent.a.onIdleDetected();
            if (activeAgent.a.isFinished()) {
                activeAgent.finilize();
            }
        }
        activeAgent = null;
    }

    @Override
    protected void beforeNextTick() {
        for (AgentStateStack a : getControlledAgents().values()) {
            activeAgent = a.current();
            activeAgent.a.onMailBoxEmpty();
            if (activeAgent.a.isFinished()) {
                activeAgent.finilize();
            }
        }

        activeAgent = null;
    }

    @Override
    public void send(Message m, int recepientAgent) {
        if (!isControlling(m.getRecepient())) {
            if (exec.informationStream().hasListeners(ExternalMessageSentInfo.class)) {
                exec.informationStream().write(new ExternalMessageSentInfo(m.getMessageId(), pid(), recepientAgent, m.getName(), exec.data().getCcCount()[pid()]));
            }
        } else {
            if (exec.informationStream().hasListeners(InternalMessageSentInfo.class)) {
                exec.informationStream().write(new InternalMessageSentInfo(m.getMessageId(), m.getSender(), recepientAgent, m.getName(), exec.data().getCcCount()[pid()]));
            }
        }

        super.send(m, recepientAgent);
    }

    @Override
    public void receive(AZIPMessage message) {
        if (!isControlling(message.getData().getSender())) {
            if (exec.informationStream().hasListeners(ExternalMessageReceivedInfo.class)) {
                exec.informationStream().write(new ExternalMessageReceivedInfo(message.getData().getMessageId(), message.getData().getSender(), pid(), message.getData().getName()));
            }
        } else {
            if (exec.informationStream().hasListeners(InternalMessageReceivedInfo.class)) {
                exec.informationStream().write(new InternalMessageReceivedInfo(message.getData().getMessageId(), message.getData().getSender(), message.getData().getRecepient(), message.getData().getName()));
            }
        }

        super.receive(message);
    }

}
