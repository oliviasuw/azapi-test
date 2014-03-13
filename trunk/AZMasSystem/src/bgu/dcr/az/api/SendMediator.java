package bgu.dcr.az.api;

import bgu.dcr.az.mas.cp.CPAgentController;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * a middle way object used to send a message you should never directly create
 * an instance of this class the usage of this class should be through the
 * method send of simple agent - simpleAgent.send("Message",
 * arg1,...,argn).to(recipient);
 *
 * @author bennyl
 *
 */
public class SendMediator {

    private Object[] args;
    private String messageName;
    private final Agent agent;
    private final CPAgentController controller;

    public SendMediator(Agent agent, CPAgentController controller) {
        this.controller = controller;
        this.agent = agent;
        args = null;
        messageName = null;
    }

    void setArgs(Object[] args) {
        this.args = args;
    }

    void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    /**
     * send the message to the given array of agents
     *
     * @param agents
     */
    public void to(int... agents) {
        for (int a : agents) {
            Message msg = new Message(messageName, agent.getId(), args, a);
            agent.beforeMessageSending(msg);
            controller.send(msg, a);
        }
    }

    public void toCurrentMessageSender() {
        to(agent.getCurrentMessage().getSender());
    }

    public void toFirstAgent() {
        to(0);
    }

    public void toLastAgent() {
        to(controller.getGlobalProblem().getNumberOfVariables() - 1);
    }

    /**
     * send the message to the next agent in the defined order
     */
    public void toNextAgent() {
        to(agent.getId() + 1);
    }

    /**
     * send the message to the previous agent in the defined order
     */
    public void toPreviousAgent() {
        to(agent.getId() - 1);
    }

    /**
     * send the message to all the agents that came after the sending agent in
     * the defined order
     */
    public void toAllAgentsAfterMe() {
        IntStream.range(agent.getId() + 1, agent.getNumberOfVariables()).forEach(this::to);
    }

    /**
     * send the message to the sending agent neighbores in the problem p while
     * neighbores are all the agents that constrainted with the sending agent
     *
     * @param p
     */
    public void toNeighbores() {
        controller.getGlobalProblem().getNeighbors(agent.getId()).forEach(this::to);
    }

    /**
     * send the message to all the given variables
     *
     * @param all
     */
    public void toAll(Collection<Integer> all) {
        all.forEach(this::to);
    }

    /**
     * send this message to all other agents (excluding the sending agent!)
     */
    public void broadcast() {
        IntStream.range(0, agent.getNumberOfVariables()).filter(i -> i != agent.getId()).forEach(this::to);
    }
}
