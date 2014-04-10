/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;

/**
 *
 * @author Shl
 */
public class SimpleDrawer implements DrawerInterface {

    private GroupBoundingQuery boundingQuery;
    private Location location;
    private double scale = 1;
    
    

    public SimpleDrawer(GroupBoundingQuery boundingQuery) {
        this.boundingQuery = boundingQuery;
        this.location = new Location();
    }

    @Override
    public void draw() {
        for (String group : boundingQuery.getGroups()) {
            GroupDrawer drawer = (GroupDrawer) boundingQuery.getMetaData(group, GroupDrawer.class);
            if (drawer != null) {
                drawer.draw();
            }
        }
    }

    @Override
    public GroupBoundingQuery getQuery() {
        return boundingQuery;
    }

    @Override
    public Location getViewPortLocation() {
        return location;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public void setViewPortLocation(double x, double y) {
        this.location.xProperty().set(x);
        this.location.yProperty().set(y);
    }

}
