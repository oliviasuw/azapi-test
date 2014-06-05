/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.statistics.base;

import bgu.dcr.az.common.ui.panels.FXMessagePanel;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.ui.ConfigurationEditor;
import bgu.dcr.az.conf.utils.ConfigurationUtils;

import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.DefaultExperimentRoot;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.misc.NoDataPane;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author User
 * @title basic
 * @index 0000
 */
@RegisterController("statistics.pages.basic")
public class BasicStatisticsScreen extends FXMLController {

    @FXML
    TreeView testsTree;

    @FXML
    BorderPane resultsContainer;

    @FXML
    Button showChartButton;

    @FXML
    Button showTableButton;

    @FXML
    BorderPane statConfigurationPanel;

    private StatisticsPlotter plotter;

    @Override
    protected void onLoadView() {
        plotter = new StatisticsPlotter(resultsContainer);

        resultsContainer.setCenter(new NoDataPane("Select statistic and click ''plot chart''/ ''Data table'' to show data."));
        putInConfigurationPanel(FXMessagePanel.createNoDataPanel("No Configuration Needed"));

        testsTree.setShowRoot(false);
        testsTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        testsTree.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null && !((TreeItem) nv).isLeaf()) {
                testsTree.getSelectionModel().select(testsTree.getRoot()); //fix bug of double clicking the title
                testsTree.getSelectionModel().clearSelection();
            } else if (nv != null) {
                selectStatistic(((StatisticCollector) ((TreeItem) nv).getValue()));
            }
        });

        testsTree.expandedItemCountProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number nv) -> {
            testsTree.getSelectionModel().clearSelection();
        });

        showChartButton.setOnAction(e -> plot());
        showTableButton.setOnAction(e -> plotTable());

        installTestsTree((DefaultExperimentRoot) require(ModularExperiment.class).execution());

    }

    private StatisticCollector getSelectedStatisticCollector() {
        TreeItem selection = (TreeItem) testsTree.getSelectionModel().getSelectedItem();
        if (selection != null) {
            return (StatisticCollector) selection.getValue();
        } else {
            return null;
        }
    }

    private Test getSelectedExperiment() {
        TreeItem selection = (TreeItem) testsTree.getSelectionModel().getSelectedItem();
        if (selection != null) {
            ExperimentToStringWrapper experiment = (ExperimentToStringWrapper) selection.getParent().getValue();
            return experiment.test;
        } else {
            return null;
        }
    }

    private void plot() {
        StatisticCollector collector = getSelectedStatisticCollector();

        if (collector != null) {
            collector.plot(plotter, getSelectedExperiment());
        } else {
            Notifications.create()
                    .title("Cannot Complete Operation")
                    .text("you must select statistic first.")
                    .showWarning();
        }
    }

    private void plotTable() {
        StatisticCollector collector = getSelectedStatisticCollector();

        if (collector != null) {
            plotter.setAsTable(true);
            collector.plot(plotter, getSelectedExperiment());
            plotter.setAsTable(false);
        } else {
            Notifications.create()
                    .title("Cannot Complete Operation")
                    .text("you must select statistic first.")
                    .showWarning();
        }
    }

    public void installTestsTree(DefaultExperimentRoot exp) {
        TreeItem testsRoot = new TreeItem("Tests");

        ModularExperiment experiment = require(ModularExperiment.class);

        for (ExecutionTree test : experiment.execution()) {
            TreeItem experimentRoot = new TreeItem(new ExperimentToStringWrapper((Test) test));

            testsRoot.getChildren().add(experimentRoot);
            for (StatisticCollector statistic : test.requireAll(StatisticCollector.class)) {
                experimentRoot.getChildren().add(new TreeItem(statistic));
            }

            experimentRoot.setExpanded(true);
        }

        Platform.runLater(() -> testsTree.setRoot(testsRoot));
    }

    private void selectStatistic(StatisticCollector statisticCollector) {
        try {
            final ConfigurationEditor configurationEditor = new ConfigurationEditor(ConfigurationUtils.load(statisticCollector), false, false, null);
            putInConfigurationPanel(configurationEditor);
        } catch (ClassNotFoundException | ConfigurationException ex) {
            putInConfigurationPanel(FXMessagePanel.createNoDataPanel("Needed Configuration Cannot Be Resolved"));
            Dialogs.create()
                    .showException(ex);
        }
    }

    private void putInConfigurationPanel(final Node node) {
        BorderPane.setAlignment(node, Pos.TOP_CENTER);
        BorderPane.setMargin(node, new Insets(0));
        statConfigurationPanel.setCenter(node);
    }

    private class ExperimentToStringWrapper {

        Test test;

        public ExperimentToStringWrapper(Test t) {
            this.test = t;
        }

        @Override
        public String toString() {
            return "Test " + test.getName();
        }

    }

}
