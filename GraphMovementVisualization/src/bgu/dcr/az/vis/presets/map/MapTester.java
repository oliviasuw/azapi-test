/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import bgu.dcr.az.vis.controls.ui.PlayerControls;
import bgu.dcr.az.vis.newplayer.NewPlayer;
import bgu.dcr.az.vis.player.api.FramesStream;
import bgu.dcr.az.vis.player.impl.BoundedFramesStream;
import bgu.dcr.az.vis.player.impl.SimplePlayer;
import bgu.dcr.az.vis.presets.map.drawer.SimpleDrawer;
import data.events.api.SimulatorEvent;
import data.events.impl.Tick;
import data.events.impl.test.EventsTester;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
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
    private GroupBoundingQuery boundingQuery;
    private SimpleDrawer drawer;

    @Override
    public void start(Stage stage) throws Exception {
        
        boundingQuery = new GroupBoundingQuery();
        drawer = new SimpleDrawer(boundingQuery);
        
        //change to beershevagraph.txt to get beersheva back
        //graph2_1.txt is telaviv
        vs = new MapVisualScene(100, "graph2_1.txt", boundingQuery, drawer);
        
        BoundedFramesStream fs = new BoundedFramesStream(10);
        FramesGenerator fg = new FramesGenerator(fs);
//        SimplePlayer player = new SimplePlayer(vs, 1000, 0);

        NewPlayer player = new NewPlayer(boundingQuery, drawer, 1000, 0);

        BorderPane bp = new BorderPane();
        bp.setCenter(vs);
        bp.setBottom(new PlayerControls(player));

        Scene scene = new Scene(bp);
//        Camera camera = new PerspectiveCamera(false);
//        scene.setCamera(camera);
//        bp.getChildren().add(camera);

//        camera.setTranslateZ(-400);
//        camera.setTranslateY(190);
//        camera.getTransforms().add(new Rotate(40, Rotate.X_AXIS));
//        camera.setRotate(360);
//        camera.setRotationAxis(new Point3D(0.7, 0.4 , 0));
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
//            eventTester.write();

        }

        @Override
        public void run() {
            Tick tick = eventTester.read();
            Collection<SimulatorEvent> read = tick.getEvents();
            while (!read.isEmpty() && !isStopped) {
                eventTester.AddNewMovesFromTick(tick, stream);
                tick = eventTester.read();
            }
//
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
