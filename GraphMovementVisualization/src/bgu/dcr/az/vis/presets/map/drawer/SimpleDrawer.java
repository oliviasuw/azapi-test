/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class SimpleDrawer implements DrawerInterface {

    private final GroupBoundingQuery boundingQuery;
    private final Location location;
    private double scale = 1;
    private double viewPortWidth;
    private double viewPortHeight;

    public SimpleDrawer(GroupBoundingQuery boundingQuery) {
        this.boundingQuery = boundingQuery;
        this.location = new Location();
    }

    @Override
    public void draw() {

        for (String group : boundingQuery.getGroups()) {
            GroupDrawer drawer = (GroupDrawer) boundingQuery.getMetaData(group, GroupDrawer.class);
            if (drawer != null && drawer.isDrawable(group)) {
                if (group.equals("GRAPH") || group.equals("DYNAMIC_COLORED")) {
//                    System.out.printf("locationX:%f, locationY:%f, scale:%f, width:%f, height:%f\n", this.location.getX(), this.location.getY(), this.scale, this.viewPortWidth, this.viewPortHeight );
                    CanvasLayer canvasLayer = (CanvasLayer) boundingQuery.getMetaData(group, CanvasLayer.class);
                    Canvas canvas = canvasLayer.getCanvas();
                    GraphicsContext gc = canvas.getGraphicsContext2D();
//                    gc.setFill(new Color(0, 0, 0, 1));
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    if (group.equals("GRAPH")) {
                        gc.save();
                        Color color = new Color(220 / 255, 220 / 255, 220 / 255, 0.1).deriveColor(160 / 255, 0, 207 / 255, 1);
                        gc.setFill(color);
                        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                        gc.restore();
                    }
                    gc.strokeText("scale: " + getScale() + " meter/pixel", 14, canvas.getHeight() - 25);
                }
                drawer.draw(group);
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

    @Override
    public void setViewPortWidth(double viewPortWidth) {
        this.viewPortWidth = viewPortWidth;
    }

    @Override
    public void setViewPortHeight(double viewPortHeight) {
        this.viewPortHeight = viewPortHeight;
    }

    @Override
    public double getViewPortWidth() {
        return viewPortWidth;
    }

    @Override
    public double getViewPortHeight() {
        return viewPortHeight;
    }

    /**
     * translates a given view port by a given delta (in pixels)
     *
     * @param dx
     * @param dy
     */
    public void moveViewPort(double dx, double dy) {
        location.xProperty().set(location.xProperty().get() + dx);
        location.yProperty().set(location.yProperty().get() + dy);
    }

}
