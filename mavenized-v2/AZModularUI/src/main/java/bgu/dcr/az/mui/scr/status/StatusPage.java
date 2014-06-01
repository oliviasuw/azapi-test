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
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.modules.StatusSyncer;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @title Status
 */
@RegisterController("main.pages.status")
public class StatusPage extends FXMLController {

    @FXML
    BorderPane cpuTimeChartContainer;

    @FXML
    BorderPane coreUsageChartContainer;

    @FXML
    BorderPane experimentViewContainer;

    @FXML
    ProgressBar progressBar;

    @FXML
    ListView<Test> testsList;

    @FXML
    Label executionNumberLabel;

    @FXML
    SplitPane split;

//    List<SubExperimentListCell> updateableListCells = new LinkedList<>();
    AtomicBoolean listUpToDate = new AtomicBoolean(true);

//    RealtimeJFXPlotter pieChartPlotter;
//    RealtimeJFXPlotter barChartPlotter;
    ConfigurationEditor experimentView;

    @Override
    protected void onLoadView() {

        ExecutionTree experimentRoot = require(ModularExperiment.class).execution();
        DefaultExperimentProgress progress = require(StatusSyncer.class).getProgress().get(DefaultExperimentProgress.class);

        installTestList(experimentRoot, progress);
        installExperimentView();
        installProgressPane(progress);

        //plotterPoke = new UIPoke(this::updateStatistics, 1000);
    }

    public static boolean accept(BaseController c) {
        return c.isInstalled(StatusSyncer.class) && c.isInstalled(ModularExperiment.class);
    }

//
//    public void setModel(final Experiment exp) {
//        
//        loadListOfSubExperiments(exp);
//        Platform.runLater(() -> {
//            createProgressUpdater(exp);
//            createListSelectionUpdater();
//            updateSelectedExperiment();
//        });
//    }
//
//    private void createListSelectionUpdater() {
//        testsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ObservableValue ov, Object t, Object t1) {
//                updateSelectedExperiment();
//                pieChartPlotter = new RealtimeJFXPlotter(cpuTimeChartContainer);
//                barChartPlotter = new RealtimeJFXPlotter(coreUsageChartContainer);
//                updateStatistics();
//            }
//        });
//    }
//
//    private void createProgressUpdater(Experiment exp) {
//        final long numExecutions = exp.numberOfExecutions();
//
//        AppController.getEventServer().listeners().add(new ExperimentStatusEventListener() {
//
//            @Override
//            public void onExperimentStarted() {
//
//            }
//
//            @Override
//            public void onSubExperimentStarted(String name) {
//                Platform.runLater(() -> {
//                    if (!testsList.getItems().isEmpty()) {
//
//                        for (Object e : testsList.getItems()) {
//                            if (((Experiment) e).getName().equals(name)) {
//                                testsList.getSelectionModel().select(e);
//                                break;
//                            }
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onSubExperimentEnded(String name) {
//            }
//
//            @Override
//            public void onExperimentEnded() {
//                progressBar.setProgress(1);
//            }
//
//            @Override
//            public void onNumberOfFinishedExecutionsChanged(int numberOfFinishedExecutions) {
//                progressBar.setProgress((double) numberOfFinishedExecutions / numExecutions);
//                updateTestsList();
//                plotterPoke.poke();
//                Platform.runLater(() -> executionNumberLabel.setText("Execution " + numberOfFinishedExecutions + " of " + numExecutions));
//            }
//
//        });
//    }
//
//    private void updateStatistics() {
//        String selectedTest = getSelectedTestName();
//        if (selectedTest != null && pieChartPlotter != null) {
//            AlgorithmCPUTimeStatisticCollector stat = AppController.getRuntimeStatisticsService().getAlgorithmCPUTimeStatistic(selectedTest);
//            if (stat != null) {
//                stat.plot(pieChartPlotter, null);
//            }
//        }
//
//        if (selectedTest != null && barChartPlotter != null) {
//            NumberOfCoresInUseStatisticCollector stat = AppController.getRuntimeStatisticsService().getNumberOfCoresInUseStatistic(selectedTest);;
//            if (stat != null) {
//                stat.plot(barChartPlotter, null);
//            }
//        }
//    }
//
//    private void updateTestsList() {
//        if (listUpToDate.compareAndSet(true, false)) {
//            Platform.runLater(() -> {
//                updateableListCells.stream()
//                        .filter(u -> u.getItem() != null)
//                        .forEach(u -> u.updateItem(u.getItem(), false));
//                listUpToDate.set(true);
//            });
//        }
//    }
//
//    private void loadListOfSubExperiments(Experiment exp) {
//        testsList.getItems().addAll(exp.subExperiments());
//        testsList.setCellFactory(p -> {
//            final SubExperimentListCell lcell = new SubExperimentListCell();
//            updateableListCells.add(lcell);
//            return lcell;
//        });
//
//    }
//
//    private String getSelectedTestName() {
//        final Experiment selection = getSelectedExperiment();
//        if (selection != null) {
//            return selection.getName();
//        }
//
//        return null;
//    }
//
//    private Experiment getSelectedExperiment() {
//        return (Experiment) testsList.getSelectionModel().getSelectedItem();
//    }
//
//    private void updateSelectedExperiment() {
//        Experiment selection = getSelectedExperiment();
//        if (selection != null) {
//            try {
//                experimentView.setModel(ConfigurationUtils.load(selection), true,
//                        p -> p.parent() == null || !StatisticCollector.class.isAssignableFrom(p.parent().configuredType()));
//
//            } catch (ClassNotFoundException | ConfigurationException ex) {
//                Notification.exception(ex);
//            }
//        }
//    }
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
            executionNumberLabel.setText("Execution " + progress.getCurrentExecutedExecutionNumeber() + " of " + progress.getNumberOfExecutions());
        });
    }

}
