/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.player.api;

import javafx.beans.property.DoubleProperty;

/**
 *
 * @author Shl
 */
public interface Layer {
    
    DoubleProperty widthProperty();

    DoubleProperty heightProperty();
    
    DoubleProperty translateXProperty();
    
    DoubleProperty translateYProperty();

    DoubleProperty translateZProperty();
    
    DoubleProperty scaleProperty();
    
    void refresh();
}
