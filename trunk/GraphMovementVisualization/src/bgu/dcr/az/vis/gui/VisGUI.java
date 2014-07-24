/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.gui;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

/**
 *
 * @author Shlomi
 */
public class VisGUI extends Application {

    public static ArrayList<LineChart<Number, Number>> charts;
    public static ScrollPane graphPane;
    
    @Override
    public void start(Stage stage) throws Exception {
//        charts = CPass1.run50x50(new FcCbjDac(), new BmCbj());
//        AlgorithmData data = SudokuGenerator.generate();
//        AlgorithmData data = SudokuGenerator.generateSpecific();
//        System.out.println(data);
        
        
        SplitPane splitPane = new SplitPane();
        graphPane = new ScrollPane();
        graphPane.setId("graphPane");
        Parent root = FXMLLoader.load(getClass().getResource("cpass1gui.fxml"));
//        LineChart<Number, Number> lineChart = chart();
//        splitPane.getItems().addAll(root,lineChart);
//        ArrayList<LineChart<Number, Number>> charts = CPass1.run50x50(new BackMarking(), new BmCbj());
        splitPane.getItems().addAll(root, graphPane);
        splitPane.setOrientation(Orientation.VERTICAL);
        Scene scene = new Scene(splitPane, 555, 800);
        stage.setTitle("Agent Based Traffic Simulator");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void setOnPane(Node nd) {
        synchronized (graphPane) {
            graphPane.setContent(nd);
        }   
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
