/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Zovadi
 */
public class SlidingAnimator extends StackPane {

    private Rectangle2D boxBounds = new Rectangle2D(0, 0, 500, 50);

    private StackPane bottomPane;
    private StackPane topPane;
    private Rectangle clipRect;
    private Timeline timelineUp;
    private Timeline timelineDown;

    public SlidingAnimator(Node top, Node bot) {
        configureBox(top, bot);

        setUpAnimation();

        autosize();

        heightProperty().addListener((p, ov, nv) -> {
            boxBounds = new Rectangle2D(0, 0, boxBounds.getWidth(), nv.doubleValue());
            setUpAnimation();
        });
        widthProperty().addListener((p, ov, nv) -> {
            boxBounds = new Rectangle2D(0, 0, nv.doubleValue(), boxBounds.getWidth());
            setUpAnimation();
        });
    }

    private void configureBox(Node top, Node bot) {
     // BOTTOM PANE 
        bottomPane = new StackPane();
        bottomPane.getChildren().add(bot);

        // TOP PANE 
        topPane = new StackPane();
        topPane.getChildren().add(top);

//        container.getChildren().addAll(bottomPane, topPane);
        getChildren().addAll(bottomPane, topPane);
    }

    private void setUpAnimation() {
        // Initially hiding the Top Pane
        clipRect = new Rectangle();
        clipRect.setWidth(boxBounds.getWidth());
        clipRect.setHeight(0);
        clipRect.translateYProperty().set(boxBounds.getHeight());
        topPane.setClip(clipRect);
        topPane.translateYProperty().set(-boxBounds.getHeight());

        // Animation for bouncing effect.
        final Timeline timelineBounceDwn = new Timeline();
        timelineBounceDwn.setCycleCount(2);
        timelineBounceDwn.setAutoReverse(true);
        final KeyValue kv1BDwn = new KeyValue(clipRect.heightProperty(), (boxBounds.getHeight() - 15));
        final KeyValue kv2BDwn = new KeyValue(clipRect.translateYProperty(), 15);
        final KeyValue kv3BDwn = new KeyValue(topPane.translateYProperty(), -15);
        final KeyFrame kf1BDwn = new KeyFrame(Duration.millis(150), kv1BDwn, kv2BDwn, kv3BDwn);
        timelineBounceDwn.getKeyFrames().add(kf1BDwn);

        // Event handler to call bouncing effect after the scroll down is finished.
        EventHandler<ActionEvent> onFinishedDwn = t -> timelineBounceDwn.play();

        timelineDown = new Timeline();
        timelineUp = new Timeline();

        // Animation for scroll down.
        timelineDown.setCycleCount(1);
        timelineDown.setAutoReverse(true);
        final KeyValue kvDwn1 = new KeyValue(clipRect.heightProperty(), boxBounds.getHeight());
        final KeyValue kvDwn2 = new KeyValue(clipRect.translateYProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(topPane.translateYProperty(), 0);
        final KeyValue kvDwn4 = new KeyValue(bottomPane.translateYProperty(), -boxBounds.getHeight());
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), onFinishedDwn, kvDwn1, kvDwn2, kvDwn3, kvDwn4);
        timelineDown.getKeyFrames().add(kfDwn);

        // Animation for bouncing effect.
        final Timeline timelineBounceUp = new Timeline();
        timelineBounceUp.setCycleCount(2);
        timelineBounceUp.setAutoReverse(true);
        final KeyValue kv1BUp = new KeyValue(clipRect.heightProperty(), (boxBounds.getHeight() - 15));
        final KeyValue kv2BUp = new KeyValue(clipRect.translateYProperty(), 15);
        final KeyValue kv3BUp = new KeyValue(bottomPane.translateYProperty(), -15);
        final KeyFrame kf1BUp = new KeyFrame(Duration.millis(150), kv1BUp, kv2BUp, kv3BUp);
        timelineBounceUp.getKeyFrames().add(kf1BUp);

        // Event handler to call bouncing effect after the scroll down is finished.
        EventHandler<ActionEvent> onFinishedUp = t -> timelineBounceUp.play();

        // Animation for scroll up.
        timelineUp.setCycleCount(1);
        timelineUp.setAutoReverse(true);
        final KeyValue kvUp1 = new KeyValue(clipRect.heightProperty(), 0);
        final KeyValue kvUp2 = new KeyValue(clipRect.translateYProperty(), boxBounds.getHeight());
        final KeyValue kvUp3 = new KeyValue(topPane.translateYProperty(), -boxBounds.getHeight());
        final KeyValue kvUp4 = new KeyValue(bottomPane.translateYProperty(), 0);
        final KeyFrame kfUp = new KeyFrame(Duration.millis(200), onFinishedUp, kvUp1, kvUp2, kvUp3, kvUp4);
        timelineUp.getKeyFrames().add(kfUp);
    }

    public void scrollDown() {
        timelineDown.play();
    }

    public void scrollUp() {
        timelineUp.play();
    }
}
