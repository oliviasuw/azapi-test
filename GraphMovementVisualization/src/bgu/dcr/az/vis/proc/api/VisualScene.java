/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

import java.util.Collection;
import javafx.animation.KeyValue;

/**
 *
 * @author Shl
 */
public interface VisualScene {
    
    Layer getLayer(long id);
    
    Collection<Layer> getLayers();

    Entity getEntity(long id);
    
    Collection<Entity> getEntities();
    
    void addDelayedTransformation(KeyValue transformation);
    
    void refresh();
}
