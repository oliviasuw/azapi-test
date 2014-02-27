/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.Layer;
import bgu.dcr.az.vis.player.api.VisualScene;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleLayer implements Layer {

    private final VisualScene scene;

    public SimpleLayer(VisualScene scene) {
        this.scene = scene;
    }

    @Override
    public VisualScene getVisualScene() {
        return scene;
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return scene.widthProperty();
    }

    public double getWidth() {
        return widthProperty().get();
    }

    public ReadOnlyDoubleProperty heightProperty() {
        return scene.heightProperty();
    }

    public double getHeight() {
        return heightProperty().get();
    }

    public ReadOnlyDoubleProperty scaleProperty() {
        return scene.scaleProperty();
    }

    public double getScale() {
        return scaleProperty().get();
    }

    public ReadOnlyDoubleProperty rotationProperty() {
        return scene.rotationProperty();
    }

    public double getRotation() {
        return rotationProperty().get();
    }

    public ReadOnlyDoubleProperty translateXProperty() {
        return scene.translateXProperty();
    }

    public double getTranslateX() {
        return translateXProperty().get();
    }

    public ReadOnlyDoubleProperty translateYProperty() {
        return scene.translateYProperty();
    }

    public double getTranslateY() {
        return translateYProperty().get();
    }

}
