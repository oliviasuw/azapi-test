/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bc.dsl.SwingDSL;
import bgu.dcr.az.execs.api.experiments.ExecutionResult;
import bgu.dcr.az.common.timing.TimingUtils;
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.execs.statistics.StatisticsManagerImpl;
import bgu.dcr.az.dcr.api.modules.Logger;
import bgu.dcr.az.dcr.execution.CPExperiment;
import bgu.dcr.az.execs.experiments.ExperimentUtils;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.ui.screens.MainWindow;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import bgu.dcr.az.ui.screens.log.LogScreen;
import bgu.dcr.az.ui.screens.problem.ProblemViewScreenCtl;
import bgu.dcr.az.ui.screens.statistics.MainStatisticScreen;
import bgu.dcr.az.ui.screens.status.StatusScreenCtl;
import bgu.dcr.az.ui.screens.status.RuntimeStatisticsService;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javax.swing.JFrame;
import nu.xom.ParsingException;
import org.controlsfx.dialog.Dialogs;
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
    private static RuntimeStatisticsService runtimeStatisticsService;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            runningExperiment = (CPExperiment) ExperimentUtils.loadExperiment(new FileInputStream(args[0]));
        } else {
            runningExperiment = loadExperiment();
        }
        updateServer = new ExperimentStatusUpdateServer(UPDATE_INTERVAL, runningExperiment);
        eventServer = new ExperimentStatusEventObserver();
        updateServer.listeners().add(eventServer);

        addProgressNotificationListener();
        runtimeStatisticsService = new RuntimeStatisticsService();
        runningExperiment.supply(RuntimeStatisticsService.class, runtimeStatisticsService);

        long time = System.currentTimeMillis();
        startTestingUI();
        System.out.println("Time To Build UI: " + (System.currentTimeMillis() - time));

        TimingUtils.schedule(() -> {
            final ExecutionResult executionResults = runningExperiment.execute();
            System.out.println("DONE " + executionResults);
        }, 100); //give the ui time to draw itself...

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

    public static RuntimeStatisticsService getRuntimeStatisticsService() {
        return runtimeStatisticsService;
    }

    public static void startTestingUI() throws IOException {
        String agentZeroStyleSheet = AppController.class.getResource("azstyle.css").toExternalForm();
        String agentZeroStyleSheet_DEBUG = "/home/bennyl/Desktop/Agent Zero/azapi-test/AZUserInterfaceFX/src/bgu/dcr/az/ui/azstyle.css";

        SwingDSL.configureLookAndFeel();
        main = new MainWindow();
        main.setExtendedState(JFrame.MAXIMIZED_BOTH);

        FXUtils.JFXPanelWithCTL<StatusScreenCtl> statusScreenFX = FXUtils.loadFXMLForSwing(StatusScreenCtl.class, "StatusScreen.fxml");
        main.addScreen("Status", "status", statusScreenFX);
        statusScreenFX.getController().setModel(runningExperiment);

        LogScreen lscreen = new LogScreen();
        main.addScreen("Log", "log", lscreen);
        runningExperiment.supply(Logger.class, lscreen);

        JFXPanel statisticScreen = FXUtils.jfxToSwing(MainStatisticScreen.class, agentZeroStyleSheet);
//        JFXPanel statisticScreen = FXUtils.jfxToSwing(MainStatisticScreen.class);
//        FXUtils.invokeInUI(() -> {
//            FXUtils.startCSSLiveReloader(statisticScreen.getScene(), agentZeroStyleSheet_DEBUG);
//        });
        main.addScreen("Statistics", "statistics", statisticScreen);
//        ProblemViewScreen pview = new ProblemViewScreen();
//        pview.setModel(runningExperiment);
        FXUtils.JFXPanelWithCTL<ProblemViewScreenCtl> pview = FXUtils.loadFXMLForSwing(ProblemViewScreenCtl.class, "ProblemViewScreen.fxml");
        pview.getController().setModel(runningExperiment);
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

    public static EmbeddedDatabaseManager getDatabaseManager() {
        return StatisticsManagerImpl.getInstance().database();
    }

    public static void showErrorDialog(Exception ex, String title) {
        Throwable e = ex;
        while (e != null && e.getMessage().equals("see cause")) {
            e = e.getCause();
        }

        if (e == null) {
            e = ex;
        }

        Dialogs.create().title(title).masthead(null).showException(e);
    }

}
