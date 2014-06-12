/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import osm.data.api.Bounds;

/**
 *
 * @author Shl
 */
public class SimpleBounds implements Bounds {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public SimpleBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }
}
