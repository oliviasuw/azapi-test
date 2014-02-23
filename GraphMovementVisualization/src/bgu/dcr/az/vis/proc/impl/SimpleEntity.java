/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.VisualizationProcessor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleEntity implements Entity {

    private final long id;
    private final ObjectProperty<Location> locationProperty;
    private final DoubleProperty rotationProperty;
    private final DoubleProperty scaleProperty;

    public SimpleEntity(long id) {
        this.id = id;
        locationProperty = new SimpleObjectProperty<>(new Location());
        rotationProperty = new SimpleDoubleProperty(0);
        scaleProperty = new SimpleDoubleProperty(1);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public ObjectProperty<Location> locationProperty() {
        return locationProperty;
    }

    public Location getLocation() {
        return locationProperty.get();
    }

    public void setLocation(Location location) {
        locationProperty.set(location);
    }

    @Override
    public DoubleProperty rotationProperty() {
        return rotationProperty;
    }

    public double getRotation() {
        return rotationProperty.get();
    }

    public void setRotation(double angle) {
        rotationProperty.set(angle);
    }

    @Override
    public DoubleProperty scaleProperty() {
        return scaleProperty;
    }

    public double getScale() {
        return scaleProperty.get();
    }

    public void setScale(double scale) {
        scaleProperty.set(scale);
    }

    @Override
    public final void draw(VisualizationProcessor processor) {
        Canvas canvas = processor.getCanvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double drawX = (getLocation().getX() - canvas.getTranslateX()) * canvas.getScaleX();
        double drawY = (getLocation().getY() - canvas.getTranslateY()) * canvas.getScaleY();

        gc.save(); 
        gc.translate(drawX, drawY);
        gc.rotate(getRotation());
        gc.scale(getScale(), getScale());
        gc.scale(canvas.getScaleX(), canvas.getScaleY());
        _draw(gc);
        gc.restore(); 
    }

    protected abstract void _draw(GraphicsContext gc);
}