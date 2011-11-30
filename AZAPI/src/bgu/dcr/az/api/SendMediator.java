package bgu.dcr.az.api;

import bgu.dcr.az.api.exp.UnRegisteredAgentException;
import java.util.Collection;

/**
 * a middle way object used to send a message
 * you should never directly create an instance of this class
 * the usage of this class should be through the method send of simple agent 
 * - simpleAgent.send("Message", arg1,...,argn).to(recipient); 
 * @author bennyl
 *
 */
public class SendMediator {

    private Message msg;
    private Mailer mailer;
    private ImmutableProblem curp;
    private String agentGroupKey;

    public SendMediator(Message msg, Mailer mailer, ImmutableProblem curp, String agentGroupKey) {
        this.msg = msg;
        this.mailer = mailer;
        this.curp = curp;
        this.agentGroupKey = agentGroupKey;
    }

    /**
     * send the message to the given array of agents
     * @param agents
     */
    public void to(int... agents) {
        for (int a : agents) {
            try {
                mailer.send(msg, a, agentGroupKey);
            } catch (IndexOutOfBoundsException ex) {
                throw new UnRegisteredAgentException("the agent with the id " + a + " is not registered (" + agentGroupKey + ")", ex);
            }
        }

    }

    /**
     * send the message to the next agent in the defined order 
     */
    public void toNextAgent() {
        to(msg.getSender() + 1);
    }

    /**
     * send the message to the previous agent in the defined order
     */
    public void toPreviousAgent() {
        to(msg.getSender() - 1);
    }

    /**
     * send the message to all the agents that came after the sending agent in the defined order
     */
    public void toAllAgentsAfterMe() {
        for (int i = msg.getSender() + 1; i < curp.getNumberOfVariables(); i++) {
            to(i);
        }
    }

    /**
     * send the message to the sending agent neighbores in the problem p
     * @param p
     */
    public void toNeighbores(ImmutableProblem p) {
        for (int n : p.getNeighbors(msg.getSender())) {
            to(n);
        }
    }

    /**
     * attach metadata to the message pirior to sending it
     * @param name
     * @param val
     * @return
     */
    public SendMediator withMeta(String name, Object val) {
        msg.getMetadata().put(name, val);
        return this;
    }

    /**
     * send the message to all the given variables
     * @param all 
     */
    public void toAll(Collection<Integer> all) {
        for (Integer i : all) {
            to(i);
        }
    }
}
