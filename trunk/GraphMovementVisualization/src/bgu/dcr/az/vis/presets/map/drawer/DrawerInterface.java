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
     * sets the scale factors (defines the ratio between meters inside one
     * pixel)
     *
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
     *
     * @param x
     * @param y
     */
    public void setViewPortLocation(double x, double y);

    /**
     * set the view port width in local coordinates (pixels)
     *
     * @param width
     */
    public void setViewPortWidth(double width);

    /**
     * set the view port height in local coordinates (pixels)
     *
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

    /**
     * Translates world coordinate to a view coordinate. Our assumption that
     * zero world coordinate always mapped to zero view coordinate.
     *
     * @param x
     * @param y
     * @return
     */
    public default Location worldToView(double x, double y) {
        return new Location(x * getScale(), y * getScale());
    }

    /**
     * Translates world coordinate to a frame (view coordinate in given frame)
     * coordinate. Our assumption that zero world coordinate always mapped to
     * zero view coordinate.
     *
     * @param x
     * @param y
     * @return
     */
    public default Location worldToFrame(double x, double y) {
        Location res = worldToView(x, y);
        res.xProperty().set(res.getX() - getViewPortLocation().getX());
        res.yProperty().set(res.getY() - getViewPortLocation().getY());
        
        return res;
    }

    /**
     * Translates view coordinate to a world coordinate. Our assumption that
     * zero world coordinate always mapped to zero view coordinate.
     *
     * @param x
     * @param y
     * @return
     */
    public default Location viewToWorld(double x, double y) {
        return new Location(x / getScale(), y / getScale());
    }

    /**
     * Translates frame coordinate (view coordinate in given frame)to a world
     * coordinate. Our assumption that zero world coordinate always mapped to
     * zero view coordinate.
     *
     * @param x
     * @param y
     * @return
     */
    public default Location frameToWorld(double x, double y) {
        return viewToWorld(x + getViewPortLocation().getX(), y + getViewPortLocation().getY());
    }

}
