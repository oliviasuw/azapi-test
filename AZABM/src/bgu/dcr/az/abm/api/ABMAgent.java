/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

/**
 *
 * @author Eran
 */
public interface ABMAgent {

    /**
     * register a new behavior to be applied to this agent, assumes that the
     * agent is not running and there is no tick currently executing
     *
     * @param behavior
     */
    void registerBehavior(Behavior behavior);

    /**
     * register a new behavior to be applied to this agent, assumes that the
     * agent is not running and there is no tick currently executing
     *
     * @param behavior
     */
    void unregisterBehavior(Behavior behavior);

    /**
     * @return the agent id (unique)
     */
    int getId();
}
