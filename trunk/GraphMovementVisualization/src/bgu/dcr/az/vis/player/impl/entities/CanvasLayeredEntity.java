/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.entities;

import bgu.dcr.az.vis.player.api.Layer;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Shl
 */
public abstract class CanvasLayeredEntity extends SimpleEntity {

    public CanvasLayeredEntity(long entityId, Class<? extends Layer> layerClazz) {
        super(entityId, layerClazz);
    }

    @Override
    protected final void _draw(Layer layer) {
        if (layer instanceof CanvasLayer) {
            CanvasLayer canvasLayer = (CanvasLayer)layer;
            Canvas canvas = canvasLayer.getCanvas();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double drawX = (getLocation().getX() - canvas.getTranslateX()) * canvasLayer.getScale();
            double drawY = (getLocation().getY() - canvas.getTranslateY()) * canvasLayer.getScale();

            gc.save();
            gc.translate(drawX, drawY);
            gc.rotate(getRotation());
            gc.rotate(canvasLayer.getRotation());
            gc.scale(getScale(), getScale());
            gc.scale(canvasLayer.getScale(), canvasLayer.getScale());
            _draw(gc);
            gc.restore();
        } else {
            throw new UnsupportedOperationException("Canvas layered entity could not be drawn on " + layer.getClass().getSimpleName() + " canvas");
        }
    }

    protected abstract void _draw(GraphicsContext gc);
}