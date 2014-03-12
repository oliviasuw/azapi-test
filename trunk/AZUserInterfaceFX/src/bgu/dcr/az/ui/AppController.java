/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bc.dsl.SwingDSL;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.mas.cp.CPExperiment;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.exp.ExperimentUtils;
import bgu.dcr.az.mas.misc.Logger;
import bgu.dcr.az.ui.screens.MainWindow;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import bgu.dcr.az.ui.screens.log.LogScreen;
import bgu.dcr.az.ui.screens.problem.ProblemViewScreen;
import bgu.dcr.az.ui.screens.statistics.BasicStatisticsScreenCtl;
import bgu.dcr.az.ui.screens.statistics.MainStatisticScreen;
import bgu.dcr.az.ui.screens.status.StatusScreenCtl;
import bgu.dcr.az.ui.screens.status.AlgorithmCPUTimeStatisticCollector;
import bgu.dcr.az.ui.screens.status.NumberOfCoresInUseStatisticCollector;
import bgu.dcr.az.ui.screens.status.RuntimeStatisticsService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javax.swing.JFrame;
import nu.xom.ParsingException;
import resources.img.ResourcesImg;

/**
 *
 * @author User
 */
public class AppController {

    public static final Image WHOOPY_DIMA = ResourcesImg.png("success-message");
    public static final Image AMAZED_DIMA = ResourcesImg.png("test-passed-message");
    public static final Image GREETING_DIMA = ResourcesImg.png("greeting-message");
    public static final Image EXCEPTION_DIMA = ResourcesImg.png("exception-message");
    public static final Image INFORMATIVE_DIMA = ResourcesImg.png("info-message-notification");

    private static final int UPDATE_INTERVAL = 100;
    private static CPExperiment runningExperiment;
    private static ExperimentStatusUpdateServer updateServer;
    private static ExperimentStatusEventObserver eventServer;
    private static MainWindow main;
    private static RuntimeStatisticsService runtimeStatistics;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        runningExperiment = loadExperiment();
        updateServer = new ExperimentStatusUpdateServer(UPDATE_INTERVAL, runningExperiment);
        eventServer = new ExperimentStatusEventObserver();
        updateServer.listeners().add(eventServer);

        runtimeStatistics = new RuntimeStatisticsService();
        runningExperiment.supply(RuntimeStatisticsService.class, runtimeStatistics);

        addProgressNotificationListener();

        long time = System.currentTimeMillis();
        startTestingUI();
        System.out.println("Time To Build UI: " + (System.currentTimeMillis() - time));
        final ExecutionResult executionResults = runningExperiment.execute();
        System.out.println("DONE " + executionResults);

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

    public static RuntimeStatisticsService getRuntimeStatistics() {
        return runtimeStatistics;
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

        JFXPanel statisticsScreen = new JFXPanel();// new MainStatisticScreen();
        FXUtils.invokeInUI(() -> {
            statisticsScreen.setScene(new Scene(new MainStatisticScreen()));
            statisticsScreen.getScene().getStylesheets().add(AppController.class.getResource("azstyle.css").toExternalForm());
        });
        //FXUtils.startCSSLiveReloader(statisticsScreen.getScene(), "C:\\Users\\User\\Desktop\\Projects\\AgentZero\\trunk\\AZUserInterfaceFX\\src\\bgu\\dcr\\az\\ui\\azstyle.css");
        main.addScreen("Statistics", "statistics", statisticsScreen);

        ProblemViewScreen pview = new ProblemViewScreen();
        pview.setModel(runningExperiment);
        main.addScreen("Problem", "problem", pview);

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
