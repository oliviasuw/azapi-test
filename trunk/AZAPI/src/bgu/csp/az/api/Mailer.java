package bgu.csp.az.api;

import bgu.csp.az.api.exp.UnRegisteredAgentException;

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
    void register(Agent agent);

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
    void send(Message msg, int to) throws UnRegisteredAgentException;

    /**
     * broadcast a message to all agents (except the sending agent)
     * @param msg
     */
    void broadcast(Message msg);

    /**
     * remove agent with the given id from the registered list, 
     * if the mailer callected statistics for this agent those statistics should not be removed
     * @param id 
     */
    public void unRegister(Integer id);

    /**
     * @param id
     * @return true if agent with the currosponding id is registered
     */
    public boolean isRegistered(Integer id);

    /**
     * wait until agent with the corrusponding id is registered
     * this function mostly needed for "Inner Agent Runners"
     * @param id
     * @throws InterruptedException 
     */
    public void waitFor(int id) throws InterruptedException;

    /**
     * @return true if all the registered mailboxed are empty
     */
    public boolean isAllMailBoxesAreEmpty();
}
