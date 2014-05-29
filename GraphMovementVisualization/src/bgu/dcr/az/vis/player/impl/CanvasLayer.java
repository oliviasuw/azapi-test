/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.VisualScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class CanvasLayer extends SimpleLayer {

    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 1);

    private final Canvas canvas;

    public CanvasLayer(VisualScene scene) {
        super(scene);
        this.canvas = new Canvas();
        this.canvas.widthProperty().bind(getVisualScene().widthProperty());
        this.canvas.heightProperty().bind(getVisualScene().heightProperty());
    }

    public Canvas getCanvas() {
        return canvas;
    }
    
    @Override
    public void refresh() {
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.setFill(BACKGROUND_COLOR);
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
