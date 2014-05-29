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
public abstract class GroupDrawer implements GroupDrawerInterface {

    protected DrawerInterface drawer;
    protected Location viewPortLocation;
    protected double viewPortScale;

    public GroupDrawer(DrawerInterface drawer) {
        this.drawer = drawer;
        this.viewPortLocation = new Location();
        this.viewPortScale = 1;
        this.viewPortLocation.xProperty().set(drawer.getViewPortLocation().getX());
        this.viewPortLocation.yProperty().set(drawer.getViewPortLocation().getY());
        this.viewPortScale = drawer.getScale();
    }

    @Override
    public void draw(String group) {
        Location newLocation = drawer.getViewPortLocation();
        double newScale = drawer.getScale();
        GroupBoundingQuery boundingQuery = drawer.getQuery();

        for (String subgroup : boundingQuery.getSubGroups(group)) {
            if (!boundingQuery.isMoveable(group, subgroup) && newLocation.getX() == viewPortLocation.getX() && newLocation.getY() == viewPortLocation.getY() && viewPortScale == newScale) {
                continue;
            }
            _draw(group, subgroup);
        }
        viewPortLocation.xProperty().set(newLocation.getX());
        viewPortLocation.yProperty().set(newLocation.getY());
        viewPortScale = newScale;
    }

    public abstract void _draw(String group, String subgroup);

    //should this drawer draw the group.
    //this exists only to solve the clear-rect problem...
    public boolean toDraw(String group) {
        Location newLocation = drawer.getViewPortLocation();
        double newScale = drawer.getScale();
        boolean moveable = drawer.getQuery().isMoveable(group);
        return (moveable || newLocation.getX() != viewPortLocation.getX() || newLocation.getY() != viewPortLocation.getY() || viewPortScale != newScale);
    }

}
