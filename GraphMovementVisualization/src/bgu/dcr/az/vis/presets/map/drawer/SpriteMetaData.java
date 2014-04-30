/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import javafx.scene.image.Image;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class SpriteMetaData {

    private HashMap<String, SingleSpriteMetaData> tagToMetaData;
    
    public enum SizeParameter {

        WIDTH, HEIGHT;
    }

    public SpriteMetaData() {
        tagToMetaData = new HashMap<>();
        putSpriteMetaData("default", new SingleSpriteMetaData(new Image(R.class.getResourceAsStream("default_sprite.png"))));
    }

    public SpriteMetaData(HashMap<String, SingleSpriteMetaData> tagToMetaData) {
        this.tagToMetaData = tagToMetaData;
        putSpriteMetaData("default", new SingleSpriteMetaData(new Image(R.class.getResourceAsStream("default_sprite.png"))));
    }
    
    public SingleSpriteMetaData getSpriteMetaData(String spriteTag) {
        return tagToMetaData.get(spriteTag);
    }
    
    public void putSpriteMetaData(String spriteTag, SingleSpriteMetaData metaData) {
        tagToMetaData.put(spriteTag, metaData);
    }
    
    public boolean hasMetaData(String spriteTag) {
        return (tagToMetaData.get(spriteTag) != null);
    }

    public static class SingleSpriteMetaData {

        private final Image image;
        private double width;
        private double height;

        public SingleSpriteMetaData(Image image) {
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        public SingleSpriteMetaData(String filepath) throws FileNotFoundException {
            this.image = new Image(new FileInputStream(filepath));
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        public SingleSpriteMetaData(Image image, SizeParameter type, double param) {
            this.image = image;
            setDefinedSize(type, param);
        }

        public SingleSpriteMetaData(Image image, double realWidth, double realHeight) {
            this.image = image;
            setDefinedSize(realWidth, realHeight);
        }

        public SingleSpriteMetaData(String filepath, SizeParameter type, double param) throws FileNotFoundException {
            this.image = new Image(new FileInputStream(filepath));
            setDefinedSize(type, param);
        }

        public SingleSpriteMetaData(String filepath, double realWidth, double realHeight) throws FileNotFoundException {
            this.image = new Image(new FileInputStream(filepath));
            setDefinedSize(realWidth, realHeight);
        }

        public final void setDefinedSize(SizeParameter type, double param) {
            if (type == SizeParameter.HEIGHT) {
                this.height = param;
                this.width = image.getWidth() * (param / image.getHeight());
            } else {
                this.width = param;
                this.height = image.getHeight() * (param / image.getWidth());
            }
        }

        public final void setDefinedSize(double realWidth, double realHeight) {
            this.width = realWidth;
            this.height = realHeight;
        }

        public Image getImage() {
            return image;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

    }
}
