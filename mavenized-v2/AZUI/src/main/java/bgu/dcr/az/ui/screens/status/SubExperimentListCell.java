/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.status;

import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentStatusSnapshot;
import bgu.dcr.az.ui.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author User
 */
public class SubExperimentListCell extends ListCell<Experiment> {

    Label label = new Label("");
    ProgressBar progressRect = new ProgressBar();
    BorderPane content = new BorderPane();

    public SubExperimentListCell() {
        BorderPane.setMargin(progressRect, new Insets(2));
        BorderPane.setMargin(progressRect, new Insets(2, 0, 0, 0));
        BorderPane.setAlignment(label, Pos.BOTTOM_LEFT);

        progressRect.setMaxWidth(Integer.MAX_VALUE);
        progressRect.setPrefHeight(3);
        progressRect.setMinHeight(3);
        progressRect.getStyleClass().add("mini-progress-bar");

        content.setBottom(progressRect);
        content.setCenter(label);
    }

    @Override
    public void updateItem(Experiment t, boolean empty) {
        super.updateItem(t, empty);
        if (t == null) {
            return;
        }
        label.textProperty().set("Test: " + t.getName());

        ExperimentStatusSnapshot knownStatus = AppController.getEventServer().getCurrentlyKnownStatus();
        
        if (knownStatus.finishedSubExperimentNames().contains(t.getName())) {
            progressRect.setProgress(1);
        } else if (t.getName().equals(knownStatus.currentExecutedSubExperimentName())) {
            final double value = (double) knownStatus.currentExecutedSubExperimentStatus().finishedExecutions() / t.numberOfExecutions();
            progressRect.setProgress(value);
        } else {
            progressRect.setProgress(0);
        }
        
        setGraphic(content);
        
    }

}
