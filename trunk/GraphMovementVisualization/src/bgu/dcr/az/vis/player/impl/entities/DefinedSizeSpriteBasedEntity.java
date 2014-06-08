/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.entities;

import bgu.dcr.az.vis.player.api.Layer;
import data.map.impl.wersdfawer.groupbounding.HasId;
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
public class DefinedSizeSpriteBasedEntity extends SpriteBasedEntity {

    private double realWidth;
    private double realHeight;

    public enum SizeParameter {

        WIDTH, HEIGHT;
    }

    public DefinedSizeSpriteBasedEntity(String entityId, String filepath, double realWidth, double realHeight) throws FileNotFoundException {
        this(entityId, new FileInputStream(filepath), realWidth, realHeight);
    }

    public DefinedSizeSpriteBasedEntity(String entityId, InputStream in, double realWidth, double realHeight) {
        this(entityId, new Image(in), realWidth, realHeight);
    }

    public DefinedSizeSpriteBasedEntity(String entityId, Image image, double realWidth, double realHeight) {
        super(entityId, image);
        this.realWidth = realWidth;
        this.realHeight = realHeight;
    }

    public DefinedSizeSpriteBasedEntity(String entityId, Class<? extends Layer> layerClazz, String filepath, SizeParameter type, double param) throws FileNotFoundException {
        this(entityId, layerClazz, new FileInputStream(filepath), type, param);
    }

    public DefinedSizeSpriteBasedEntity(String entityId, Class<? extends Layer> layerClazz, InputStream in, SizeParameter type, double param) {
        this(entityId, new Image(in), type, param);
    }

    /**
     * sets one size parameter in meters. the other will be determined according to image width-height ratio.
     * @param entityId
     * @param image
     * @param type
     * @param param 
     */
    public DefinedSizeSpriteBasedEntity(String entityId, Image image, SizeParameter type, double param) {
        super(entityId, image);
        if (type == SizeParameter.HEIGHT) {
            this.realHeight = param;
            this.realWidth = image.getWidth() * (param / image.getHeight());
        } else {
            this.realWidth = param;
            this.realHeight = image.getHeight() * (param / image.getWidth());
        }
    }

    public double getRealWidth() {
        return realWidth;
    }

    public double getRealHeight() {
        return realHeight;
    }
    
}
