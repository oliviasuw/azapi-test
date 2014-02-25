/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.test;

import bgu.dcr.az.vis.proc.impl.BasicOperationsFrame;
import bgu.dcr.az.vis.proc.impl.CanvasLayer;
import bgu.dcr.az.vis.proc.impl.Location;
import bgu.dcr.az.vis.proc.impl.SimplePlayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Zovadi
 */
public class TrafficLoadTester extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TrafficLoadVisualScene vs = new TrafficLoadVisualScene();
        BasicOperationsFrame frame = new BasicOperationsFrame();
        frame.move(0, new Location(100, 100), new Location(500, 500));
        frame.move(0, new Location(500, 500), new Location(100, 100));
        frame.rotate(0, 0, 3600);

        Pane pane = new Pane();
        pane.setPrefSize(800, 600);
        vs.getLayers().forEach(l -> {
            if (l instanceof CanvasLayer) {
                final Canvas canvas = ((CanvasLayer) l).getCanvas();
                canvas.setWidth(pane.getPrefWidth());
                canvas.setHeight(pane.getPrefHeight());
                pane.getChildren().add(canvas);
                System.out.println("Added layer: " + l);
            }
        });
        
        SimplePlayer player = new SimplePlayer(vs, 1000, 0);
        
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        
        player.play(frame);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
