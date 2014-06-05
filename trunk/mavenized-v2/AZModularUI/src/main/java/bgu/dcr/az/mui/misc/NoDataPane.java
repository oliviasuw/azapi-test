/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.misc;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import resources.img.ResourcesImg;

/**
 *
 * @author bennyl
 */
public class NoDataPane extends BorderPane {

    public static final Image NO_DATA = ResourcesImg.png("no-data");

    private Label label;

    public NoDataPane(String text) {
        getStyleClass().add("no-data-pane");
        label = new Label(text);
        label.setGraphic(new ImageView(NO_DATA));
        label.setContentDisplay(ContentDisplay.TOP);

        setCenter(label);
    }

}
