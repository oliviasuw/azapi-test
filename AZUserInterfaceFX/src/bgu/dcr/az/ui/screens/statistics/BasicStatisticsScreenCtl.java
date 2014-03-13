/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.common.ui.panels.FXMessagePanel;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.stat.StatisticCollector;
import bgu.dcr.az.ui.confe.ConfigurationEditor;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author User
 */
public class BasicStatisticsScreenCtl implements Initializable {

    @FXML
    HBox header;

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

    StatisticsPlotter plotter;
    private StatisticCollector selectedStatisticCollector;
    private MainStatisticScreen statisticScreen;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        plotter = new StatisticsPlotter(resultsContainer);

        resultsContainer.setCenter(FXMessagePanel.createNoDataPanel("No Data To Show Currently"));
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

    }

    private StatisticCollector getSelectedStatisticCollector() {
        TreeItem selection = (TreeItem) testsTree.getSelectionModel().getSelectedItem();
        if (selection != null) {
            return (StatisticCollector) selection.getValue();
        } else {
            return null;
        }
    }

    private Experiment getSelectedExperiment() {
        TreeItem selection = (TreeItem) testsTree.getSelectionModel().getSelectedItem();
        if (selection != null) {
            ExperimentToStringWrapper experiment = (ExperimentToStringWrapper) selection.getParent().getValue();
            return experiment.exp;
        } else {
            return null;
        }
    }

    private void plot() {
        StatisticCollector collector = getSelectedStatisticCollector();

        if (collector != null) {
            collector.plot(plotter, getSelectedExperiment());
        } else {
            Notification.Notifier.INSTANCE.notifyWarning("Cannot Complete Operation", "you must select statistic first.");
        }
    }

    private void plotTable() {
        StatisticCollector collector = getSelectedStatisticCollector();

        if (collector != null) {
            plotter.setAsTable(true);
            collector.plot(plotter, getSelectedExperiment());
            plotter.setAsTable(false);
        } else {
            Notification.Notifier.INSTANCE.notifyWarning("Cannot Complete Operation", "you must select statistic first.");
        }
    }

    public void setModel(Experiment exp) {
        TreeItem testsRoot = new TreeItem("Tests");

        for (Experiment sub : exp) {
            TreeItem experimentRoot = new TreeItem(new ExperimentToStringWrapper(sub));
            testsRoot.getChildren().add(experimentRoot);
            for (StatisticCollector statistic : sub.getStatistics()) {
                experimentRoot.getChildren().add(new TreeItem(statistic));
            }

            experimentRoot.setExpanded(true);
        }

        Platform.runLater(() -> testsTree.setRoot(testsRoot));
    }

    private void selectStatistic(StatisticCollector statisticCollector) {
        this.selectedStatisticCollector = statisticCollector;
        try {
            final ConfigurationEditor configurationEditor = new ConfigurationEditor(ConfigurationUtils.load(statisticCollector), false, false, null);
            putInConfigurationPanel(configurationEditor);
        } catch (ClassNotFoundException | ConfigurationException ex) {
            putInConfigurationPanel(FXMessagePanel.createNoDataPanel("Needed Configuration Cannot Be Resolved"));
            Notification.exception(ex);
        }
    }

    private void putInConfigurationPanel(final Node node) {
        BorderPane.setAlignment(node, Pos.TOP_CENTER);
        BorderPane.setMargin(node, new Insets(0));
//        if (node instanceof Control) {
//            ((Control) node).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//            ((Control) node).setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//        }
        statConfigurationPanel.setCenter(node);
    }

    private class ExperimentToStringWrapper {

        Experiment exp;

        public ExperimentToStringWrapper(Experiment exp) {
            this.exp = exp;
        }

        @Override
        public String toString() {
            return "Test " + exp.getName();
        }

    }

    public void setStatisticScreen(MainStatisticScreen statisticScreen) {
        this.statisticScreen = statisticScreen;
    }

}
