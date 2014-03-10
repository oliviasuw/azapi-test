/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

import bgu.dcr.az.mas.impl.HasSolution;
import bgu.dcr.az.mas.stat.InfoStream;
import java.util.Collection;

/**
 *
 * @author Eran
 */
public interface World extends HasSolution {

    InfoStream infoStream();
    
    /**
     * @return the set of behaviors that attached to this world *and* this world
     * meet their requirements
     */
    Collection<Class<? extends Behavior>> behaviors();

    Collection<WorldService> services();

    Collection<ABMAgent> agents();

    /**
     * searches for an agent with the given id
     *
     * @param id
     * @return the found agent or null if no such agent found
     */
    ABMAgent findAgent(int id);

    /**
     * adds a new agent to the world
     *
     * @return the new agent id
     */
    int addAgent();

    /**
     * adds a new agent to the world and supply its initial data
     *
     * @param data
     * @return the new agent id
     */
    default int addAgent(AgentData... data) {
        int id = addAgent();
        setAgentData(id, data);
        return id;
    }

    /**
     * supply initial data to an agent with the given id, if this agent already
     * has data it will be replaced.
     *
     * @param id
     * @param data
     */
    void setAgentData(int id, AgentData... data);

    /**
     * add behavior definition to this world
     *
     * @param b
     */
    void addBehavior(Class<? extends Behavior> b);
}
