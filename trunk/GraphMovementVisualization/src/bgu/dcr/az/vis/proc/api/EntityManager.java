/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.api;

import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface EntityManager {
    
    Collection<Entity> getAll();
    
    Entity getEntity(long id);
    
    void addEntity(Entity entity);
    
}
