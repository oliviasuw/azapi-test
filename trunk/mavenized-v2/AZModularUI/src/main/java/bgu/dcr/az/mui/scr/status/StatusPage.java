/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.status;

import bgu.dcr.az.common.exceptions.UnexpectedException;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.ui.ConfigurationEditor;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.modules.StatusSyncer;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @title Status
 */
@RegisterController("main.pages.status")
public class StatusPage extends FXMLController {

    @FXML
    BorderPane experimentViewContainer;

    @FXML
    BorderPane miniDashContainer;

    @FXML
    ProgressBar progressBar;

    @FXML
    ListView<Test> testsList;

    @FXML
    Label executionNumberLabel;

    @FXML
    SplitPane split;

    ConfigurationEditor experimentView;

    @Override
    protected void onLoadView() {

        ExecutionTree experimentRoot = require(ModularExperiment.class).execution();
        DefaultExperimentProgress progress = require(StatusSyncer.class).getProgress().get(DefaultExperimentProgress.class);

        installTestList(experimentRoot, progress);
        installExperimentView();
        installProgressPane(progress);
        installMinidash();
    }

    public static boolean accept(BaseController c) {
        return c.isInstalled(StatusSyncer.class) && c.isInstalled(ModularExperiment.class);
    }

    private void installTestList(ExecutionTree root, DefaultExperimentProgress prog) {
        testsList.setCellFactory(items -> new TestProgressCell(prog));

        for (ExecutionTree test : root) {
            testsList.getItems().add((Test) test);
        }

        infoStream().listen(StatusSyncer.Sync.class, sync -> {
            testsList.lookupAll(".list-cell").stream().forEach(v -> ((TestProgressCell) v).update());
        });
    }

    private void installExperimentView() {
        experimentView = new ConfigurationEditor();
        BorderPane.setAlignment(experimentView, Pos.TOP_CENTER);
        BorderPane.setMargin(experimentView, new Insets(0));
        experimentViewContainer.setCenter(experimentView);

        testsList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Test> observable, Test oldValue, Test newValue) -> {
            if (newValue == null) {
                throw new NullPointerException("test cannot be null");
            } else {
                loadExperimentView(newValue);
            }
        });

        if (!testsList.getItems().isEmpty()) {
            testsList.getSelectionModel().selectFirst();
        }
    }

    private void loadExperimentView(Test newValue) {
        try {
            experimentView.setModel(ConfigurationUtils.load(newValue), true,
                    p -> p.parent() == null || !StatisticCollector.class.isAssignableFrom(p.parent().configuredType()));
        } catch (ClassNotFoundException | ConfigurationException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private void installProgressPane(DefaultExperimentProgress progress) {
        infoStream().listen(StatusSyncer.Sync.class, sync -> {
            progressBar.setProgress(progress.getExperimentProgress());
            executionNumberLabel.setText("Execution " + (progress.getCurrentExecutedExecutionNumeber()+1) + " of " + progress.getNumberOfExecutions());
        });
    }

    private void installMinidash() {
        Controller<Node> minidash = ControllerRegistery.get().createController("status.minidash", this);
        if (minidash != null) {
            miniDashContainer.setCenter(minidash.getView());
        }else {
            System.err.println("minidash not found...");
        }
    }

}
