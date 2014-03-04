/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

import java.util.Collection;

/**
 *
 * @author Eran
 */
public interface World {

    Collection<ABMAgent> existingAgents();

    Collection<Behavior> appliedBehaviors();

    Collection<Service> existingServices();
}
