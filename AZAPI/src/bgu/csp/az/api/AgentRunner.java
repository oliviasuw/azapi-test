/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import bgu.csp.az.api.agt.SimpleAgent;

/**
 *
 * @author bennyl
 */
public interface AgentRunner extends Runnable {
    
    /**
     * this method supports the nested agents feature, 
     * an nested agent can call this method to "change the skin" of the agent with $originalAgentId
     * as for this moment all the messages that will get sent by this type of agent (type = upper level class) 
     * will received by this new nested agent, you should only nest agent of the same type once - means 
     * let A, B, C be agent types , if A 'nested in' B 'nested in' C then A!=B, A!=C, B!=C (all the types are different).
     * Note: this method is blocking - means that the you will resume your code only after $nestedAgent will call finish. 
     *
     * if $nestedAgent called Panic (directly or indirectly by throwing an exception) all the nested agents will be discarded 
     * and the agent runner will stop.
     * 
     * in the current time no "non self stopeable" algorithms can be excuted as nested agents, 
     * the idle detector is still not designed to work with multipule nested agents - in next versions (if it will be requested)
     * we will add support for this feature (Implementor Comment: by adding mailGroupKey in the idle detector)
     * @param nestedAgent 
     */
    public void nest(int originalAgentId, SimpleAgent nestedAgent);
}
