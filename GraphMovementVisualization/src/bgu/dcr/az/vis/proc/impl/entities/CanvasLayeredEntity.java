/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.entities;

import bgu.dcr.az.vis.proc.api.Layer;
import bgu.dcr.az.vis.proc.impl.CanvasLayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Shl
 */
public abstract class CanvasLayeredEntity extends SimpleEntity {

    public CanvasLayeredEntity(long entityId, long layerId) {
        super(entityId, layerId);
    }

    @Override
    protected final void _draw(Layer layer) {
        if (layer instanceof CanvasLayer) {
            Canvas canvas = ((CanvasLayer)layer).getCanvas();
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
        } else {
            throw new UnsupportedOperationException("Canvas layered entity could not be drawn on " + layer.getClass().getSimpleName() + " canvas");
        }
    }

    protected abstract void _draw(GraphicsContext gc);
}
