/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bc.dsl.SwingDSL;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.mas.cp.CPExperiment;
import bgu.dcr.az.mas.exp.ExperimentUtils;
import bgu.dcr.az.ui.screens.MainWindow;
import bgu.dcr.az.ui.screens.StatusScreen;
import java.io.IOException;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        runningExperiment = loadExperiment();
        updateServer = new ExperimentStatusUpdateServer(UPDATE_INTERVAL, runningExperiment);
        eventServer = new ExperimentStatusEventObserver();
        updateServer.listeners().add(eventServer);

        startTestingUI();
        System.out.println("DONE " + runningExperiment.execute());
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

    public static void startTestingUI() {
        SwingDSL.configureLookAndFeel();
        final MainWindow main = new MainWindow();

        //Status Screen
        StatusScreen statusScreen = new StatusScreen();
        statusScreen.setModel(runningExperiment);
        main.addScreen("Status", "status", statusScreen);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                main.setVisible(true);
            }
        });
    }

    private static CPExperiment loadExperiment() throws ConfigurationException, IOException, ParsingException, ClassNotFoundException {
        return (CPExperiment) ExperimentUtils.loadExperiment(AppController.class.getResourceAsStream("test.xml"));
    }

}
