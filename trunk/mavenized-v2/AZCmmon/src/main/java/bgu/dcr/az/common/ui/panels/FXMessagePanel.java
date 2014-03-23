/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.common.ui.panels;

import java.awt.BorderLayout;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import resources.img.ResourcesImg;

/**
 *
 * @author User
 */
public class FXMessagePanel extends BorderPane{
    public static final Image NO_DATA_IMAGE = ResourcesImg.png("no-data");
    private Label info;

    public FXMessagePanel(String message, Image icon) {
        getStyleClass().add("message-panel");
        info = new Label(message);        
        final VBox vBox = new VBox(new ImageView(icon), info);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        vBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        BorderPane.setAlignment(vBox, Pos.CENTER);
        setCenter(vBox);
    }
    
    public static FXMessagePanel createNoDataPanel(String message){
        FXMessagePanel result = new FXMessagePanel(message, NO_DATA_IMAGE);
        result.getStyleClass().add("no-data");
        return result;
    }
    
    
}
