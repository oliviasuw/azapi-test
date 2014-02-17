/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicx.sprite;

import graphmovementvisualization.Location;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.event.ActionEvent;
import javafx.util.Duration;

/**
 *
 * @author Shl
 */
public class ObjectAnimator {
    
    private final Sprite sprite;

    public ObjectAnimator(Sprite sprite) {
        this.sprite = sprite;
    }
    
    public KeyFrame move(Location to) {
        return move(to.getX(), to.getY());
    }

    public KeyFrame move(double x, double y) {
        double dy = y - sprite.getLocation().getY();
        double dx = x - sprite.getLocation().getX();

        double theta = Math.atan2(dy, dx);
        theta = theta * 180.0 / Math.PI;

        System.out.println("Theta: " + theta);

        sprite.setRotation(theta);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), (ActionEvent t) -> {
        },
                new KeyValue(sprite.getLocation().xProperty(), x),
                new KeyValue(sprite.getLocation().yProperty(), y)
        );

        return keyFrame;
    }
}
