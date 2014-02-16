/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmovementvisualization;

import java.io.InputStream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 *
 * @author Shl
 */
public class Sprite {

    private Image image;
    private final Location location;
    private Timeline timeLine;
    private int index;

    public Sprite(int index) {
//        System.out.println("sprite created");
        InputStream resourceAsStream = getClass().getResourceAsStream("Ford Mustang Fastback.png");
        image = new Image(resourceAsStream);
        location = new Location();
        this.index = index;
        initTimeLine();
    }

    public Location getLocation() {
        return location;
    }

    public Image getImage() {
        return image;
    }

    public KeyFrame move(final Location to) {
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000),
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent t) {
//                        getLocation().setX(to.getX().doubleValue());
//                        getLocation().setY(to.getY().doubleValue());

                    }
                },
                new KeyValue(location.getX(), to.getX().doubleValue() - image.getWidth() / 2), new KeyValue(location.getY(), to.getY().doubleValue() - image.getHeight() / 2)
        );

        return keyFrame;
//        timeLine.getKeyFrames().add(keyFrame);
//        timeLine.play();
    }

    public KeyFrame move(double x, double y) {
        Location newLoc = new Location(x, y);
        KeyFrame keyFrams = this.move(newLoc);
        return keyFrams;
    }

    private void initTimeLine() {
//        timeLine = new Timeline();
//        timeLine.setOnFinished(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent t) {
//                timeLine.getKeyFrames().clear();
//            }
//        });
//        timeLine.setAutoReverse(false);
//        timeLine.setCycleCount(1);

    }

//    public boolean hasTimelineInstructions() {
//        return timeLine.getKeyFrames().size() > 0;
//    }
    public int getIndex() {
        return index;
    }

}
