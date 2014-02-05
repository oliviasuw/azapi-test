/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bc.dsl.SwingDSL;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.mas.cp.CPExperiment;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.exp.ExperimentUtils;
import bgu.dcr.az.ui.screens.MainWindow;
import bgu.dcr.az.ui.screens.status.StatusScreenCtl;
import bgu.dcr.az.ui.statistics.AlgorithmCPUTimeStatisticCollector;
import bgu.dcr.az.ui.statistics.NumberOfCoresInUseStatisticCollector;
import bgu.dcr.az.ui.util.FXUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nu.xom.ParsingException;

/**
 *
 * @author User
 */
public class AppController {

    private static final int UPDATE_INTERVAL = 100;
    private static CPExperiment runningExperiment;
    private static ExperimentStatusUpdateServer updateServer;
    private static ExperimentStatusEventObserver eventServer;
    private static Map<String, AlgorithmCPUTimeStatisticCollector> algorithmCPUTimeStatistics = new HashMap<>();
    private static Map<String, NumberOfCoresInUseStatisticCollector> coresInUseStatistics = new HashMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        runningExperiment = loadExperiment();
        updateServer = new ExperimentStatusUpdateServer(UPDATE_INTERVAL, runningExperiment);
        eventServer = new ExperimentStatusEventObserver();
        updateServer.listeners().add(eventServer);

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
        System.out.println("DONE " + runningExperiment.execute());

        System.gc();
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
        final MainWindow main = new MainWindow();

        FXUtils.JFXPanelWithCTL<StatusScreenCtl> statusScreenFX = FXUtils.load(StatusScreenCtl.class, "StatusScreen.fxml");
        main.addScreen("Status", "status", statusScreenFX);
        statusScreenFX.getController().setModel(runningExperiment);

        java.awt.EventQueue.invokeLater(() -> main.setVisible(true));
    }

    private static CPExperiment loadExperiment() throws ConfigurationException, IOException, ParsingException, ClassNotFoundException {
        return (CPExperiment) ExperimentUtils.loadExperiment(AppController.class.getResourceAsStream("test.xml"));
    }

}
