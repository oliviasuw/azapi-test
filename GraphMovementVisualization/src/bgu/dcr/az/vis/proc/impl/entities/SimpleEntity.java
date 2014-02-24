/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.entities;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.Layer;
import bgu.dcr.az.vis.proc.api.VisualScene;
import bgu.dcr.az.vis.proc.impl.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleEntity implements Entity {

    private final long entityId;
    private final long layerId;
    private final ObjectProperty<Location> locationProperty;
    private final DoubleProperty rotationProperty;
    private final DoubleProperty scaleProperty;

    public SimpleEntity(long entityId, long layerId) {
        this.entityId = entityId;
        this.layerId = layerId;
        locationProperty = new SimpleObjectProperty<>(new Location());
        rotationProperty = new SimpleDoubleProperty(0);
        scaleProperty = new SimpleDoubleProperty(1);
    }

    @Override
    public long getEntityId() {
        return entityId;
    }
    
    public long getLayerId() {
        return layerId;
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
    public final void draw(VisualScene scene) {
        _draw(scene.getLayer(layerId));
    }

    protected abstract void _draw(Layer layer);
}