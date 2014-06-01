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
 *
 * @author Zovadi
 */
public class SpriteBasedEntity extends SimpleEntity {

    private final Image image;

    public SpriteBasedEntity(long entityId, String filepath) throws FileNotFoundException {
        this(entityId, new FileInputStream(filepath));
    }

    public SpriteBasedEntity(long entityId, InputStream in) {
        this(entityId, new Image(in));
    }

    public SpriteBasedEntity(long entityId, Image image) {
        super(entityId);
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

}
