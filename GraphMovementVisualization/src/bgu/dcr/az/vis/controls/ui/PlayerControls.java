/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.controls.ui;

import bgu.dcr.az.vis.player.api.Player;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class PlayerControls extends BorderPane {

    public static final int BUTTON_SIZE = 32;
    public static final int ANIMATION_SPEED_WIDTH = 128;
    public static final int ANIMATION_MIN_SPEED = 4;

    public static final Image PLAY = new Image(R.class.getResourceAsStream("playback-start.png"));
    public static final Image PAUSE = new Image(R.class.getResourceAsStream("pause.png"));
    public static final Image STOP = new Image(R.class.getResourceAsStream("playback-stop.png"));
    public static final Image SEEK_BACKWARD = new Image(R.class.getResourceAsStream("seek-backward.png"));
    public static final Image SEEK_FORWARD = new Image(R.class.getResourceAsStream("seek-forward.png"));
    public static final Image SKIP_BACKWARD = new Image(R.class.getResourceAsStream("skip-backward.png"));
    public static final Image SKIP_FORWARD = new Image(R.class.getResourceAsStream("skip-forward.png"));

    private Button playPayseButton;
    private Button stopButton;
    private Button seekBackwardButton;
    private Button seekForwardButton;
    private Button skipBackwardButton;
    private Button skipForwardButton;

    private Slider playProgressSlider;

    private ButtonedSlider animationSpeedSlider;

    private final Player player;

    public PlayerControls(Player player) {
        this.player = player;

        setLeft(generatePlayeControllButtons());
        setCenter(generatePlayProgressSlider());
        setRight(generateAnimationSpeedSlider());
    }

    private HBox generatePlayeControllButtons() {
        playPayseButton = generateButton(PAUSE);
        playPayseButton.setOnAction(e -> {
            if (player.isPaused()) {
                ((ImageView) playPayseButton.getGraphic()).setImage(PAUSE);
                player.resume();
            } else {
                ((ImageView) playPayseButton.getGraphic()).setImage(PLAY);
                player.pause();
            }
        });
        stopButton = generateButton(STOP);
        seekBackwardButton = generateButton(SEEK_BACKWARD);
        seekForwardButton = generateButton(SEEK_FORWARD);
        skipBackwardButton = generateButton(SKIP_BACKWARD);
        skipForwardButton = generateButton(SKIP_FORWARD);
        HBox playerButtons = new HBox(skipBackwardButton, seekBackwardButton, playPayseButton, stopButton, seekForwardButton, skipForwardButton);
        return playerButtons;
    }

    private Button generateButton(Image image) {
        ImageView imageView = new ImageView(image);
        Button btn = new Button("", imageView);
        imageView.fitWidthProperty().bind(btn.widthProperty());
        imageView.fitHeightProperty().bind(btn.heightProperty());
        btn.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        btn.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        btn.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
        return btn;
    }

    private Node generatePlayProgressSlider() {
        playProgressSlider = new Slider();
        return playProgressSlider;
    }

    private Node generateAnimationSpeedSlider() {
        animationSpeedSlider = new ButtonedSlider(0, ANIMATION_MIN_SPEED, 0);
        animationSpeedSlider.setMinWidth(ANIMATION_SPEED_WIDTH);
        animationSpeedSlider.setPrefWidth(ANIMATION_SPEED_WIDTH);
        animationSpeedSlider.setMaxWidth(ANIMATION_SPEED_WIDTH);
        animationSpeedSlider.valueProperty().addListener((p, ov, nv) -> {
            if (nv.doubleValue() <= ANIMATION_MIN_SPEED / 2.0) {
                player.millisPerFrameProperty().setValue(Math.pow(10, ANIMATION_MIN_SPEED - nv.doubleValue()));
            } else {
                player.millisPerFrameProperty().setValue(Math.pow(10, ANIMATION_MIN_SPEED / 2.0) + (ANIMATION_MIN_SPEED / 2.0 - nv.doubleValue()));
            }
        });
        animationSpeedSlider.setValue(1);
        return animationSpeedSlider;
    }

}
