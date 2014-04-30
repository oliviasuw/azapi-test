/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.entities;

import bgu.dcr.az.vis.player.api.Layer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * a sprite entity with a defined size in meters.
 * you can decide to set a preferred width, height, or both.
 * when choosing only one parameter, the other will be determined according to the image width-height ratio.
 * notice that setting both height and width may result in a distorted image.
 * 
 * @author Shl
 */
public class DefinedSizeSpriteBasedEntity extends CanvasLayeredEntity {

    private double realWidth;
    private double realHeight;
    private final Image image;

    public enum SizeParameter {

        WIDTH, HEIGHT;
    }

    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, String filepath, double realWidth, double realHeight) throws FileNotFoundException {
        this(entityId, layerClazz, new FileInputStream(filepath), realWidth, realHeight);
    }

    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, InputStream in, double realWidth, double realHeight) {
        this(entityId, layerClazz, new Image(in), realWidth, realHeight);
    }

    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, Image image, double realWidth, double realHeight) {
        super(entityId, layerClazz);
        this.image = image;
        this.realWidth = realWidth;
        this.realHeight = realHeight;
    }

    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, String filepath, SizeParameter type, double param) throws FileNotFoundException {
        this(entityId, layerClazz, new FileInputStream(filepath), type, param);
    }

    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, InputStream in, SizeParameter type, double param) {
        this(entityId, layerClazz, new Image(in), type, param);
    }

    /**
     * sets one size parameter in meters. the other will be determined according to image width-height ratio.
     * @param entityId
     * @param layerClazz
     * @param image
     * @param type
     * @param param 
     */
    public DefinedSizeSpriteBasedEntity(long entityId, Class<? extends Layer> layerClazz, Image image, SizeParameter type, double param) {
        super(entityId, layerClazz);
        this.image = image;
        if (type == SizeParameter.HEIGHT) {
            this.realHeight = param;
            this.realWidth = image.getWidth() * (param / image.getHeight());
        } else {
            this.realWidth = param;
            this.realHeight = image.getHeight() * (param / image.getWidth());
        }
    }

    @Override
    protected final void _draw(GraphicsContext gc) {
        gc.translate(-realWidth / 2.0, -realHeight / 2.0);
        gc.drawImage(image, 0, 0, realWidth, realHeight);
    }

    public double getRealWidth() {
        return realWidth;
    }

    public double getRealHeight() {
        return realHeight;
    }

    public Image getImage() {
        return image;
    }

    
    
}
