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

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }
    
    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }
}
