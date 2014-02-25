/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.proc.impl.BasicOperationsFrame;
import bgu.dcr.az.vis.proc.impl.Location;
import bgu.dcr.az.vis.proc.impl.SimplePlayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Zovadi
 */
public class MapTester extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MapVisualScene vs = new MapVisualScene(1, "graph2.txt");
        BasicOperationsFrame frame = new BasicOperationsFrame();
        frame.move(0, new Location(100, 100), new Location(500, 500));
        frame.move(0, new Location(500, 500), new Location(100, 100));
        frame.rotate(0, 0, 3600);
        
        SimplePlayer player = new SimplePlayer(vs, 100000, 0);
        
        Scene scene = new Scene(vs);
        stage.setScene(scene);
        stage.show();
        
        player.play(frame);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
