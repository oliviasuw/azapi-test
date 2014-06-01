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
public interface DrawerInterface {

    public void draw();

    public GroupBoundingQuery getQuery();

    public Location getViewPortLocation();

    public double getScale();

    public void setScale(double scale);

    public void setViewPortLocation(double x, double y);

    public void setViewPortWidth(double width);

    public void setViewPortHeight(double width);

    public double getViewPortWidth();

    public double getViewPortHeight();

}
