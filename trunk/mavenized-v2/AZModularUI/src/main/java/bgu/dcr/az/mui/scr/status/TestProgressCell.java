/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.status;

import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
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
public class TestProgressCell extends ListCell<Test> {

    Label label = new Label("");
    ProgressBar progressRect = new ProgressBar();
    BorderPane content = new BorderPane();

    DefaultExperimentProgress progress;

    public TestProgressCell(DefaultExperimentProgress progress) {
        this.progress = progress;

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

    public void update() {
        if (getItem() != null) {
            updateItem(getItem(), false);
        }
    }

    @Override
    public void updateItem(Test t, boolean empty) {
        super.updateItem(t, empty);
        if (t == null) {
            return;
        }
        label.textProperty().set("Test: " + t.getName());

        if (progress.isTestFinished(t.getName())) {
            progressRect.setProgress(1);
        } else if (t.getName().equals(progress.getCurrentTestName())) {
            progressRect.setProgress(progress.getCurrentTestProgress());
        } else {
            progressRect.setProgress(0);
        }

        setGraphic(content);

    }

}
