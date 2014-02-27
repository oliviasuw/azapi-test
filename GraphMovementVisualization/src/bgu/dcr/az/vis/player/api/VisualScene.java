/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.api;

import java.util.Collection;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Shl
 */
public interface VisualScene {
    
    ReadOnlyDoubleProperty widthProperty();
    
    ReadOnlyDoubleProperty heightProperty();
    
    DoubleProperty translateXProperty();
    
    DoubleProperty translateYProperty();
    
    DoubleProperty rotationProperty();
    
    DoubleProperty scaleProperty();
    
    Layer getLayer(long id);
    
    Collection<? extends Layer> getLayers();

    Entity getEntity(long id);
    
    Collection<? extends Entity> getEntities();
}
