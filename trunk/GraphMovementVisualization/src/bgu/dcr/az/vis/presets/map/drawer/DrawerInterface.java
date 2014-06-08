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

    /**
     * 
     * @return the scale factors (the ratio between meters inside one pixel)
     */
    public double getScale();

    /**
     * sets the scale factors (defines the ratio between meters inside one pixel)
     * @param scale 
     */
    public void setScale(double scale);

    /**
     * 
     * @return the view port location in local coordinates (pixels)
     */
    public Location getViewPortLocation();

    /**
     * sets view port location in local coordinates (pixels)
     * @param x
     * @param y 
     */
    public void setViewPortLocation(double x, double y);

    /**
     * set the view port width in local coordinates (pixels)
     * @param width
     */    
    public void setViewPortWidth(double width);

    /**
     * set the view port height in local coordinates (pixels)
     * @param height
     */
    public void setViewPortHeight(double height);

    /**
     * @return the view port width in local coordinates (pixels)
     */
    public double getViewPortWidth();

    /**
     * @return the view port height in local coordinates (pixels)
     */
    public double getViewPortHeight();

}
