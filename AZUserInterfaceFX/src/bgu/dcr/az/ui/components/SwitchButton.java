/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.components;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author User
 */
public class SwitchButton extends StackPane {

    private static final int HIDER_MARGIN = 1;

    private BorderPane buttonsPane;
    private Rectangle hidingRectangle;
    private Button bLeft, bRight;
    private Button hideButton;
    private ChangeListener hideButtonSizeChangeListener;

    public SwitchButton() {

        setAlignment(Pos.CENTER_LEFT);

        buttonsPane = new BorderPane();
        hidingRectangle = new Rectangle();
        bLeft = new Button("Advance");
        bRight = new Button("Simple ");

        getStyleClass().add("switch-button");
        hidingRectangle.getStyleClass().add("hider");
        buttonsPane.getStyleClass().add("button-pane");

        BorderPane.setMargin(bLeft, new Insets(2));
        BorderPane.setMargin(bRight, new Insets(2));
        buttonsPane.setLeft(bLeft);
        buttonsPane.setRight(bRight);

        getChildren().add(buttonsPane);
        getChildren().add(hidingRectangle);

        //change listeners
        hideButtonSizeChangeListener = (o, ov, nv) -> adeptRectangleToHideeSize();
        bLeft.setOnAction(e -> chageHidee(bLeft, true));
        bRight.setOnAction(e -> chageHidee(bRight, true));

        hidingRectangle.setTranslateY(0);
        chageHidee(bLeft, false);

        prefHeightProperty().bind(buttonsPane.prefHeightProperty());
        buttonsPane.setPadding(new Insets(HIDER_MARGIN));

        getStylesheets().add(getClass().getResource("switch-button.css").toExternalForm());
        parentProperty().addListener((p,o,n) -> chageHidee(bLeft, true));
    }

    private void adeptRectangleToHideeSize() {
        hidingRectangle.setHeight(hideButton.getHeight() + HIDER_MARGIN * 2);
        hidingRectangle.setWidth(hideButton.getWidth() + HIDER_MARGIN * 2);
    }

    private void chageHidee(Button b, boolean animate) {

        if (hideButton != null) {
            hideButton.widthProperty().removeListener(hideButtonSizeChangeListener);
            hideButton.heightProperty().removeListener(hideButtonSizeChangeListener);
        }
        Button old = hideButton;

        hideButton = b;
        hideButton.widthProperty().addListener(hideButtonSizeChangeListener);
        hideButton.heightProperty().addListener(hideButtonSizeChangeListener);
        adeptRectangleToHideeSize();

        if (animate) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), hidingRectangle);
            transition.setToX(hideButton.getLayoutX() - HIDER_MARGIN);
            transition.setFromX(old.getLayoutX() - HIDER_MARGIN);
            transition.play();
        } else {
            hidingRectangle.setTranslateX(hideButton.getLayoutX() - HIDER_MARGIN);
        }
    }

}
