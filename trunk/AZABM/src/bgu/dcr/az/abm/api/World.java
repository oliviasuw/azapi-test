/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

import bgu.dcr.az.mas.impl.HasSolution;
import java.util.Collection;

/**
 *
 * @author Eran
 */
public interface World extends HasSolution {
    /**
     * @return the set of behaviors that attached to this world *and* this world
     * meet their requirements
     */
    Collection<Class<? extends Behavior>> behaviors();

    Collection<Service> services();

    Collection<ABMAgent> agents();

    /**
     * searches for an agent with the given id
     *
     * @param id
     * @return the found agent or null if no such agent found
     */
    ABMAgent findAgent(int id);
}
