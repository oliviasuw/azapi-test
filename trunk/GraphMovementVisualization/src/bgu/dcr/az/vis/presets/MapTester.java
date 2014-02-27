/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.controls.ui.PlayerControls;
import bgu.dcr.az.vis.player.api.FramesStream;
import bgu.dcr.az.vis.player.api.Layer;
import bgu.dcr.az.vis.player.impl.BasicOperationsFrame;
import bgu.dcr.az.vis.player.impl.BoundedFramesStream;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.Location;
import bgu.dcr.az.vis.player.impl.SimplePlayer;
import data.events.api.SimulatorEvent;
import data.events.impl.test.EventsTester;
import java.util.Collection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Zovadi
 */
public class MapTester extends Application {

    private static MapVisualScene vs;

    @Override
    public void start(Stage stage) throws Exception {
        vs = new MapVisualScene(1, "graph4.txt");
        BoundedFramesStream fs = new BoundedFramesStream(10);
        FramesGenerator fg = new FramesGenerator(fs);
        SimplePlayer player = new SimplePlayer(vs, 1000, 0);

        BorderPane bp = new BorderPane();
        bp.setCenter(vs);
        bp.setBottom(new PlayerControls(player));

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
        stage.addEventFilter(WindowEvent.WINDOW_HIDING, e -> fg.cancel());

        player.play(fs);

        fg.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class FramesGenerator extends Thread {

        private final FramesStream stream;
        private boolean isStopped = false;
        private final EventsTester eventTester;

        public FramesGenerator(FramesStream stream) {
            this.stream = stream;
            MapCanvasLayer layer = (MapCanvasLayer) vs.getLayer(MapCanvasLayer.class);
            eventTester = new EventsTester(layer.getGraphData());
            eventTester.write();
        }

        @Override
        public void run() {

            Collection<SimulatorEvent> read = eventTester.read();

            while (!read.isEmpty() && !isStopped) {
                eventTester.addNewMovesFromEvents(read, stream);
                read = eventTester.read();
            }

//            long i = 0;
//            while (true) {
//                stream.writeFrame(new BasicOperationsFrame().directedMove(0, new Location(100, 100), new Location(100, 500)));
//                if (isStopped) {
//                    break;
//                }
//                stream.writeFrame(new BasicOperationsFrame().directedMove(0, new Location(100, 500), new Location(500, 500)));
//                if (isStopped) {
//                    break;
//                }
//                stream.writeFrame(new BasicOperationsFrame().directedMove(0, new Location(500, 500), new Location(500, 100)));
//                if (isStopped) {
//                    break;
//                }
//                stream.writeFrame(new BasicOperationsFrame().directedMove(0, new Location(500, 100), new Location(100, 100)));
//                if (isStopped) {
//                    break;
//                }
//                i++;
//            }
        }

        public void cancel() {
            isStopped = true;
            stop();
        }
    };
}
