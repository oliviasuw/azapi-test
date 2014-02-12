/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphmovementvisualization;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author Shl
 */
public class Location {
    
   private DoubleProperty x;
   private DoubleProperty y;

    public Location() {
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        this.x.setValue(0);
        this.y.setValue(0);
    }

    public Location(double x, double y) {
        this();
        this.x.setValue(x);
        this.y.setValue(y);
    }

    
    
    public DoubleProperty getX() {
        return x;
    }

    public void setX(double x) {
        this.x.setValue(x);
    }

    public DoubleProperty getY() {
        return y;
    }

    public void setY(double y) {
        this.y.setValue(y);
    }
    
    
}
