/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Layer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class CanvasLayer implements Layer {

    public static final Color BACKGROUND_COLOR = new Color(1, 1, 1, 1);

    private final DoubleProperty scaleProperty;
    private final Canvas canvas;

    public CanvasLayer() {
        this.scaleProperty = new SimpleDoubleProperty(1);
        this.canvas = new Canvas();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public DoubleProperty widthProperty() {
        return canvas.widthProperty();
    }

    public void setWidth(double width) {
        widthProperty().set(width);
    }

    public double getWidth() {
        return widthProperty().get();
    }

    @Override
    public DoubleProperty heightProperty() {
        return canvas.heightProperty();
    }

    public void setHeight(double height) {
        heightProperty().set(height);
    }

    public double getHeight() {
        return heightProperty().get();
    }

    @Override
    public DoubleProperty scaleProperty() {
        return scaleProperty;
    }

    public void setScale(double scale) {
        scaleProperty().set(scale);
    }

    public double getScale() {
        return scaleProperty().get();
    }

    @Override
    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
