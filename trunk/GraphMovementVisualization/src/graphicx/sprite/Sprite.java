/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicx.sprite;

import graphmovementvisualization.Location;
import java.io.InputStream;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author Shl
 */
public class Sprite {

    protected final Image image;
    protected final Location location;
    protected final long index;
    protected final ObjectAnimator animator;
    protected double angle = 0;

    public Sprite(long index) {
        InputStream resourceAsStream = getClass().getResourceAsStream("car.jpg");
        image = new Image(resourceAsStream);
        location = new Location();
        animator = new ObjectAnimator(this);
        this.index = index;
    }

    public Location getLocation() {
        return location;
    }
     
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        double drawX = location.getX() - tx;
        double drawY = location.getY() - ty;

        gc.save(); // saves the current state on stack, including the current transform
        gc.translate(drawX, drawY);
        gc.rotate(angle);
        gc.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
        gc.drawImage(image, 0, 0);
        gc.restore(); // back to original state (before rotation)

//                    gcAction.drawImage(sprite.getImage(), location.getX().doubleValue() - tx, location.getY().doubleValue() - ty);
    }

    public long getIndex() {
        return index;
    }

    public double getRotation() {
        return angle;
    }

    public void setRotation(double rotation) {
        this.angle = rotation;
    }

    public ObjectAnimator getAnimator() {
        return animator;
    }
    
}
