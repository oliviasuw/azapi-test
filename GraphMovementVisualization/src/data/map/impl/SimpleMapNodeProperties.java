/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.map.impl;

import bgu.dcr.az.vis.tools.Location;
import data.map.api.MapNodeProperties;

/**
 *
 * @author Zovadi
 */
public class SimpleMapNodeProperties implements MapNodeProperties {

    private final Location location;

    public SimpleMapNodeProperties(Location location) {
        this.location = location;
    }
    
    public SimpleMapNodeProperties(double x, double y) {
        this(new Location(x, y));
    }

    public SimpleMapNodeProperties() {
        this(new Location());
    }
    
    @Override
    public Location getLocation() {
        return location;
    }
    
}
