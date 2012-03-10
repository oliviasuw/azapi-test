package bgu.dcr.az.api;

import bgu.dcr.az.api.exp.UnRegisteredAgentException;
import bgu.dcr.az.api.infra.Execution;

/**
 * This is a familiar concept, an Interface for designing mailers – the mailer is attached to the Execution object – what means that you should be able to attach different mailers to test the algorithm with (some mailers can help with producing algorithm visualization and some can be used for debugging).
 * @author bennyl
 */
public interface Mailer {

    /**
     * register new Agent 
     * the mailbox can only send messages to registered agents 
     * @param agent
     */
    MessageQueue register(Agent agent, String groupKey);

    /**
     * remove all registered agents 
     * usecase: reset mailer 
     */
    void unregisterAll();

    /**
     * send a message to a registered agent
     * @param msg
     * @param to
     * @throws UnRegisteredAgentException  
     */
    void send(Message msg, int to, String groupKey) throws UnRegisteredAgentException;

    /**
     * broadcast a message to all agents (except the sending agent)
     * @param msg
     */
    void broadcast(Message msg, String groupKey);

    /**
     * remove agent with the given id from the registered list, 
     * @param id 
     */
    public void unRegister(int id, String groupKey);

    /**
     * @return true if all the registered mailboxed are empty
     */
    public boolean isAllMailBoxesAreEmpty(String groupKey);

    /**
     * set the execution that this mailer is responsible for
     * @param aThis 
     */
    public void setExecution(Execution aThis);
    
    /**
     * release all blocking agents from a specific mail group
     * @param mailGroup 
     */
    public void releaseAllBlockingAgents(String mailGroup);

    /**
     * release all blocking agents from all mail groups 
     * this method only make sense if you are trying to terminate the whole execution
     * as it may harm the integrity of its results
     */
    public void releaseAllBlockingAgents();
}
