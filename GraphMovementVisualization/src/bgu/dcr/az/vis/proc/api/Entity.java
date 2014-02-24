/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.api;

import bgu.dcr.az.vis.proc.impl.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;

/**
 * Represents the basic visualization entity, contains the 
 * most primitive data about it that enables applying basic 
 * actions on every an entity and visualize it
 * @author Zovadi
 */
public interface Entity {
    long getEntityId();
    
    ObjectProperty<Location> locationProperty();

    DoubleProperty rotationProperty();
    
    DoubleProperty scaleProperty();
    
    void draw(VisualScene scene);
}