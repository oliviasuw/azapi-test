/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.ui;

import azdevjfx.*;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.vis.VisualExecutionRunner;
import bgu.dcr.az.exen.AbstractTest;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.vis.VisualExecutionRunnerImpl;
import bgu.dcr.az.vdev.AZSystem;
import bgu.dcr.az.vdev.vis.NetworkTrafficVisualization;
import com.javafx.experiments.scenicview.ScenicView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Administrator
 */
public class TestUI extends Application {

    public static Class testClass = PlayScreen.class;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        primaryStage.setTitle("Hello World!");
        JFXs.maximizeWindow(primaryStage);
        
        final String fxmlFile = testClass.getSimpleName() + ".fxml";
        System.out.println("FXML File Loaded is: " + fxmlFile);
        final URL resource = TestUI.class.getResource(fxmlFile);
        System.out.println("Resource: " + resource);
        FXMLLoader loader = new FXMLLoader(resource);

        Pane root = (Pane) loader.load();
        final Scene scene = new Scene(root);

        scene.getStylesheets().add(TestUI.class.getResource("/azdevjfx/azVis.css").toExternalForm());

        PlayScreen controller = (PlayScreen) loader.getController();
        
//        VisualizationChooseScreen controller = (VisualizationChooseScreen) loader.getController();
        Experiment exp = ExperimentReader.read(new File("exp.xml"));
        VisualExecutionRunnerImpl vr = new VisualExecutionRunnerImpl(((AbstractTest) exp.getTests().get(0)).buildExecution(0));
        vr.setVisualization(new NetworkTrafficVisualization());
        AZSystem.thread(vr);
        
        controller.setup(primaryStage, vr);
//        controller.setExperiment(exp);
        
//        ScenicView.show(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void test(){
        Label l = new Label();
    }
    
}
