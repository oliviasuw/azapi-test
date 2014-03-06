/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.Entity;
import bgu.dcr.az.vis.player.api.Layer;
import bgu.dcr.az.vis.player.api.VisualScene;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleScrollableVisualScene extends ScrollPane implements VisualScene {

    private final DoubleProperty rotationProperty;
    private final DoubleProperty scaleProperty;

    private final Map<Class, Layer> layers;
    private final Map<Long, Entity> entities;

    private final Pane pane;

    public SimpleScrollableVisualScene(double contentPrefWidth, double contentPrefHeight) {
        this.rotationProperty = new SimpleDoubleProperty(0);
        this.scaleProperty = new SimpleDoubleProperty(1);

        this.layers = new HashMap<>();
        this.entities = new HashMap<>();

        pane = new Pane();
        pane.setPrefWidth(contentPrefWidth);
        pane.setPrefHeight(contentPrefHeight);
        setContent(pane);

        setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    public void registerLayer(Class<? extends Layer> clazz, Layer layer, Node layerNode) {
        layers.put(clazz, layer);

        if (layerNode != null) {
            pane.getChildren().add(layerNode);
            layerNode.toFront();

            hvalueProperty().addListener((ov, n, o) -> layerNode.translateXProperty().set(o.doubleValue() * (pane.getWidth() - getViewportBounds().getWidth())));
            vvalueProperty().addListener((ov, n, o) -> layerNode.translateYProperty().set(o.doubleValue() * (pane.getHeight() - getViewportBounds().getHeight())));
        }
    }

    @Override
    public Layer getLayer(Class<? extends Layer> clazz) {
        return layers.get(clazz);
    }

    @Override
    public Collection<? extends Layer> getLayers() {
        return layers.values();
    }

    public void addEntity(long entityId, Entity entity) {
        entities.put(entityId, entity);
    }

    @Override
    public Entity getEntity(long id) {
        return entities.get(id);
    }

    @Override
    public Collection<? extends Entity> getEntities() {
        return entities.values();
    }

    @Override
    public DoubleProperty rotationProperty() {
        return rotationProperty;
    }

    @Override
    public DoubleProperty scaleProperty() {
        return scaleProperty;
    }

    public void setContainerSize(double x, double y) {
        pane.setPrefWidth(x);
        pane.setPrefHeight(y);
    }
    
    

}
