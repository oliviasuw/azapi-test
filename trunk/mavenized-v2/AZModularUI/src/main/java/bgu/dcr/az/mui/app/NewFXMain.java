/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.app;

import bgu.dcr.az.common.exceptions.UnexpectedException;
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.execs.exps.ExperimentFailedException;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.mui.RootController;
import bgu.dcr.az.mui.modules.SyncPulse;
import bgu.dcr.az.mui.scr.main.MainPage;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author bennyl
 */
public class NewFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            RootController root = new RootController();

            long time = System.currentTimeMillis();
            System.out.println("Reading experiment... ");
            ModularExperiment exp = ModularExperiment.createDefault(getClass().getResourceAsStream("test.xml"), true);

            System.out.println("[" + (System.currentTimeMillis() - time) + "] initializing modules... ");
            time = System.currentTimeMillis();
            exp.initializeModules();

            System.out.println("[" + (System.currentTimeMillis() - time) + "] configuring UI... ");
            time = System.currentTimeMillis();
            root.install(ModularExperiment.class, exp);
            root.install(SyncPulse.class, new SyncPulse(5));

            MainPage p = root.findAndInstall("main");

            root.loadView();
            Scene scene = new Scene(p.getView());
            System.out.println("[" + (System.currentTimeMillis() - time) + "] Starting Experiment Execution... ");

            FXUtils.startCSSLiveReloader(p.getView(), "/home/bennyl/Desktop/MoreSpace/Projects/AgentZero/mavenized-v2/AZModularUI/src/main/java/bgu/dcr/az/mui/scr/AgentZero.css");

            primaryStage.setTitle("Agent Zero Testing Session");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setOnCloseRequest(e -> System.exit(0));

            new Thread(() -> {
                try {
                    exp.execute();
                } catch (ExperimentFailedException ex) {
                    FXUtils.invokeInUI(()
                            -> Dialogs.create().showException(ex));
                }
            }).start();
            primaryStage.show();

        } catch (ConfigurationException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
