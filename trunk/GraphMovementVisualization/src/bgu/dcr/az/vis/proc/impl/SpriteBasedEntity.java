/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

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

    public SpriteBasedEntity(long id, String filepath) throws FileNotFoundException {
        super(id);

        image = new Image(new FileInputStream(filepath));
    }

    public SpriteBasedEntity(long id, InputStream in) {
        super(id);

        image = new Image(in);
    }

    @Override
    protected void _draw(GraphicsContext gc) {
        gc.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
        gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
    }

}
