/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bc.dsl.SwingDSL;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.cp.CPExperiment;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.exp.ExperimentUtils;
import bgu.dcr.az.mas.misc.Logger;
import bgu.dcr.az.ui.screens.MainWindow;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import bgu.dcr.az.ui.screens.log.LogScreen;
import bgu.dcr.az.ui.screens.statistics.BasicStatisticsScreenCtl;
import bgu.dcr.az.ui.screens.statistics.StatisticsScreen;
import bgu.dcr.az.ui.screens.status.StatusScreenCtl;
import bgu.dcr.az.ui.statistics.AlgorithmCPUTimeStatisticCollector;
import bgu.dcr.az.ui.statistics.NumberOfCoresInUseStatisticCollector;
import bgu.dcr.az.ui.util.FXUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.swing.JFrame;
import nu.xom.ParsingException;
import resources.img.ResourcesImgAnchor;

/**
 *
 * @author User
 */
public class AppController {

    public static final Image WHOOPY_DIMA = new Image(ResourcesImgAnchor.class.getResourceAsStream("success-message.png"));
    public static final Image AMAZED_DIMA = new Image(ResourcesImgAnchor.class.getResourceAsStream("test-passed-message.png"));
    public static final Image GREETING_DIMA = new Image(ResourcesImgAnchor.class.getResourceAsStream("greeting-message.png"));

    private static final int UPDATE_INTERVAL = 100;
    private static CPExperiment runningExperiment;
    private static ExperimentStatusUpdateServer updateServer;
    private static ExperimentStatusEventObserver eventServer;
    private static Map<String, AlgorithmCPUTimeStatisticCollector> algorithmCPUTimeStatistics = new HashMap<>();
    private static Map<String, NumberOfCoresInUseStatisticCollector> coresInUseStatistics = new HashMap<>();
    private static MainWindow main;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        runningExperiment = loadExperiment();
        updateServer = new ExperimentStatusUpdateServer(UPDATE_INTERVAL, runningExperiment);
        eventServer = new ExperimentStatusEventObserver();
        updateServer.listeners().add(eventServer);

        addProgressNotificationListener();

        for (CPExperimentTest test : runningExperiment.getTests()) {
            final AlgorithmCPUTimeStatisticCollector collector = new AlgorithmCPUTimeStatisticCollector();
            algorithmCPUTimeStatistics.put(test.getName(), collector);
            test.getStatistics().add(collector);

            final NumberOfCoresInUseStatisticCollector nccollector = new NumberOfCoresInUseStatisticCollector();
            coresInUseStatistics.put(test.getName(), nccollector);
            test.getStatistics().add(nccollector);
        }

        long time = System.currentTimeMillis();
        startTestingUI();
        System.out.println("Time To Build UI: " + (System.currentTimeMillis() - time));
        final ExecutionResult executionResults = runningExperiment.execute();
        System.out.println("DONE " + executionResults);

//        System.gc();
    }

    public static ExperimentStatusUpdateServer getUpdateServer() {
        return updateServer;
    }

    public static ExperimentStatusEventObserver getEventServer() {
        return eventServer;
    }

    public static CPExperiment getRunningExperiment() {
        return runningExperiment;
    }

    /**
     * @return map of algorithm cpu time statistics per test name
     */
    public static Map<String, AlgorithmCPUTimeStatisticCollector> getAlgorithmCPUTimeStatistics() {
        return algorithmCPUTimeStatistics;
    }

    public static Map<String, NumberOfCoresInUseStatisticCollector> getCoresInUseStatistics() {
        return coresInUseStatistics;
    }

    public static void startTestingUI() throws IOException {
        SwingDSL.configureLookAndFeel();
        main = new MainWindow();
        main.setExtendedState(JFrame.MAXIMIZED_BOTH);

        FXUtils.JFXPanelWithCTL<StatusScreenCtl> statusScreenFX = FXUtils.load(StatusScreenCtl.class, "StatusScreen.fxml");
        main.addScreen("Status", "status", statusScreenFX);
        statusScreenFX.getController().setModel(runningExperiment);

        LogScreen lscreen = new LogScreen();
        main.addScreen("Log", "log", lscreen);
        runningExperiment.supply(Logger.class, lscreen);

        FXUtils.JFXPanelWithCTL<BasicStatisticsScreenCtl> statisticsScreen = FXUtils.load(BasicStatisticsScreenCtl.class, "BasicStatisticsScreen.fxml");
        statisticsScreen.getController().setModel(runningExperiment);
        main.addScreen("Statistics", "statistics", statisticsScreen);

        java.awt.EventQueue.invokeLater(() -> main.setVisible(true));
    }

    private static CPExperiment loadExperiment() throws ConfigurationException, IOException, ParsingException, ClassNotFoundException {
        return (CPExperiment) ExperimentUtils.loadExperiment(AppController.class.getResourceAsStream("test.xml"));
    }

    private static void addProgressNotificationListener() {
        eventServer.listeners().add(new ExperimentStatusEventListener() {

            @Override
            public void onSubExperimentEnded(String name) {
                Platform.runLater(() -> Notification.Notifier.INSTANCE.notify("Test Completed", "The Test '" + name + "' was completed successfully!", AMAZED_DIMA));
            }

            @Override
            public void onExperimentEnded() {
                if (runningExperiment.lastResult().getState() == ExecutionResult.State.SUCCESS) {
                    Platform.runLater(() -> {
                        Notification.Notifier.INSTANCE.notify("Whoopy!!!", "Execution ended without any problem", WHOOPY_DIMA, "success");
                    });
                }
            }

        });
    }

    public static void focusMainScreen() {
        main.requestFocus();
    }

}
