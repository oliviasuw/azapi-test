/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.controls.ui;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Zovadi
 */
public class ButtonedSlider extends BorderPane {
    private final Slider slider;
    
    public ButtonedSlider(double minValue, double maxValue, double currentValue) {
        double diff = Math.max(0, maxValue - minValue);
        slider = new Slider(minValue, maxValue, currentValue);
        slider.setPadding(new Insets(3));
        BorderPane.setAlignment(slider, Pos.CENTER);
        setCenter(slider);

        Button minusButton = new Button("-");
        minusButton.setMinSize(16, 16);
        minusButton.setPrefSize(16, 16);
        minusButton.setMaxSize(16, 16);
        BorderPane.setAlignment(minusButton, Pos.CENTER);
        minusButton.setOnAction(e -> slider.setValue(slider.getValue() - diff / 20.0));
        setLeft(minusButton);
        
        Button plusButton = new Button("+");
        plusButton.setMinSize(16, 16);
        plusButton.setPrefSize(16, 16);
        plusButton.setMaxSize(16, 16);
        BorderPane.setAlignment(plusButton, Pos.CENTER);
        plusButton.setOnAction(e -> slider.setValue(slider.getValue() + diff / 20.0));
        setRight(plusButton);
    }
    
    public DoubleProperty valueProperty() {
        return slider.valueProperty();
    }
    
    public double getValue() {
        return slider.getValue();
    }
    
    public void setValue(double value) {
        slider.setValue(value);
    }
}
