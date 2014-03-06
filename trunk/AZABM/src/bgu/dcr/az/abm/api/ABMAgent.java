/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

import bgu.dcr.az.abm.exen.ABMExecution;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Eran
 */
public interface ABMAgent {

    /**
     * @param type
     * @return true if the agent has data from the given type
     */
    boolean hasDataOfType(Class<? extends AgentData> type);

    /**
     * @return collection of all agent data
     */
    Collection<AgentData> data();

    /**
     * replaces the agent data with the given data
     *
     * @param data
     */
    void setData(AgentData... data);

    /**
     * add data for this agent
     *
     * @param data
     */
    void addData(AgentData data);

    default void addAllData(AgentData... data) {
        addAllData(Arrays.asList(data));
    }

    default void addAllData(Collection<AgentData> data) {
        data.forEach(this::addData);
    }

    /**
     * register a new behavior to be applied to this agent, assumes that the
     * agent is not running and there is no tick currently executing
     *
     * @param behavior
     */
    void registerBehavior(Behavior behavior);

    /**
     * un register behavior with the given type, assumes that the agent is not
     * running and there is no tick currently executing
     *
     * @param behavior
     */
    void unregisterBehavior(Class<? extends Behavior> behavior);

    default void unregisterBehaviors(Collection<Class<? extends Behavior>> behaviors) {
        behaviors.forEach(this::unregisterBehavior);
    }

    /**
     * @return collection of all the behaviors that registered to this agent
     */
    Collection<Class<? extends Behavior>> registeredBehaviors();

    /**
     * @return the agent id (unique)
     */
    int getId();

    void kill();

    /**
     * set the execution that this agent runs in
     *
     * @param execution
     */
    void setExecution(ABMExecution execution);

    /**
     * @return the current world tick number
     */
    int tickNumber();
}
