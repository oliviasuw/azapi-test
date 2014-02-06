/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.status;

import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.ui.AppController;
import bgu.dcr.az.ui.ExperimentStatusEventListener;
import bgu.dcr.az.ui.statistics.AlgorithmCPUTimeStatisticCollector;
import bgu.dcr.az.ui.statistics.NumberOfCoresInUseStatisticCollector;
import bgu.dcr.az.ui.statistics.RealtimeJFXPlotter;
import bgu.dcr.az.ui.util.UIPoke;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import syntaxhl.JSAnchor;

/**
 * FXML Controller class
 *
 * @author User
 */
public class StatusScreenCtl implements Initializable {

    @FXML
    BorderPane cpuTimeChartContainer;

    @FXML
    BorderPane coreUsageChartContainer;

    @FXML
    WebView experimentXMLView;

    @FXML
    ProgressBar progressBar;

    @FXML
    ListView testsList;

    @FXML
    Label executionNumberLabel;

    List<SubExperimentListCell> updateableListCells = new LinkedList<>();
    AtomicBoolean listUpToDate = new AtomicBoolean(true);

    RealtimeJFXPlotter pieChartPlotter;
    RealtimeJFXPlotter barChartPlotter;

    UIPoke pieChartPlotterPoke;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pieChartPlotterPoke = new UIPoke(this::updateStatistics, 1000);

        final WebEngine engine = experimentXMLView.getEngine();
        engine.getHistory().setMaxSize(0);
        engine.load(JSAnchor.class.getResource("index.html").toExternalForm());
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State t, Worker.State t1) {
                if (t1 == Worker.State.SUCCEEDED) {
                    try {
                        final Object exp = testsList.getSelectionModel().getSelectedItem();
                        if (exp != null) {
                            updateSelectedExperimentXML((Experiment) exp);
                        }
                    } finally {
                        engine.getLoadWorker().stateProperty().removeListener(this);

                    }
                }
            }
        });
    }

    public void setModel(final Experiment exp) {

        loadListOfSubExperiments(exp);
        Platform.runLater(() -> {
            createProgressUpdater(exp);
            createListSelectionUpdater();
            updateSelectedExperimentXML(exp);
        });
    }

    private void updateSelectedExperimentXML(final Experiment exp) {
        final WebEngine engine = experimentXMLView.getEngine();
        if (engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) { //otherwise this means that the callback will update this
            try {
                engine.executeScript(""
                        + "document.getElementsByTagName(\"body\")[0].innerHTML = \"<pre class='brush: xml' id='xml-data'/>\";\n");
                engine.getDocument().getElementById("xml-data").setTextContent(ConfigurationUtils.toConfigurationXMLString(exp));
                engine.executeScript("SyntaxHighlighter.highlight();");
//                                System.out.println("script execution returned: " + result);

            } catch (Exception ex) {
                Logger.getLogger(StatusScreenCtl.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void createListSelectionUpdater() {
        testsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateSelectedExperimentXML((Experiment) t1);
                pieChartPlotter = new RealtimeJFXPlotter(cpuTimeChartContainer);
                barChartPlotter = new RealtimeJFXPlotter(coreUsageChartContainer);
                updateStatistics();
            }
        });
    }

    private void createProgressUpdater(Experiment exp) {
        final long numExecutions = exp.numberOfExecutions();

        AppController.getEventServer().listeners().add(new ExperimentStatusEventListener() {

            @Override
            public void onExperimentStarted() {

            }

            @Override
            public void onSubExperimentStarted(String name) {
                Platform.runLater(() -> {
                    if (!testsList.getItems().isEmpty()) {

                        for (Object e : testsList.getItems()) {
                            if (((Experiment) e).getName().equals(name)) {
                                testsList.getSelectionModel().select(e);
                                break;
                            }
                        }
                    }
                });
            }

            @Override
            public void onSubExperimentEnded(String name) {
            }

            @Override
            public void onExperimentEnded() {
                progressBar.setProgress(1);
            }

            @Override
            public void onNumberOfFinishedExecutionsChanged(int numberOfFinishedExecutions) {
                progressBar.setProgress((double) numberOfFinishedExecutions / numExecutions);
                updateTestsList();
                pieChartPlotterPoke.poke();
                Platform.runLater(() -> executionNumberLabel.setText("Execution " + numberOfFinishedExecutions + " of " + numExecutions));
            }

        });
    }

    private void updateStatistics() {
        String selectedTest = getSelectedTestName();
        if (selectedTest != null && pieChartPlotter != null) {
            AlgorithmCPUTimeStatisticCollector stat = AppController.getAlgorithmCPUTimeStatistics().get(selectedTest);
            stat.plot(pieChartPlotter);
        }

        if (selectedTest != null && barChartPlotter != null) {
            NumberOfCoresInUseStatisticCollector stat = AppController.getCoresInUseStatistics().get(selectedTest);
            stat.plot(barChartPlotter);
        }
    }

    private void updateTestsList() {
        if (listUpToDate.compareAndSet(true, false)) {
            Platform.runLater(() -> {
                updateableListCells.stream()
                        .filter(u -> u.getItem() != null)
                        .forEach(u -> u.updateItem(u.getItem(), false));
                listUpToDate.set(true);
            });
        }
    }

    private void loadListOfSubExperiments(Experiment exp) {
        testsList.getItems().addAll(exp.subExperiments());
        testsList.setCellFactory(p -> {
            final SubExperimentListCell lcell = new SubExperimentListCell();
            updateableListCells.add(lcell);
            return lcell;
        });

    }

    private String getSelectedTestName() {
        final Experiment selection = (Experiment) testsList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            return selection.getName();
        }

        return null;
    }

}
