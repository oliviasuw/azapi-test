/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmovementvisualization;

import graphics.sprite.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import data.graph.impl.GraphReader;
import graphics.graph.GraphDrawer;
import graphics.sprite.Car;
import data.events.api.SimulatorEvent;
import data.events.api.SimulatorTick;
import data.events.impl.EventReader;
import data.events.impl.EventWriter;
import data.graph.impl.GraphData;
import data.events.impl.MoveEvent;
import data.events.impl.TickEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 *
 * @author Shl
 */
public class GraphMovementVisualization extends Application {

    private final ArrayList<Sprite> sprites = new ArrayList<>();

    final Random r = new Random();
    private GraphData graphData = new GraphData();
    private ScrollPane scrollPane;
    private final double MAX_SCALE = 1;
    private final double MIN_SCALE = 0.2;
    private double scale = 1;
    private EventWriter eventWriter;
    private EventReader eventReader;

    @Override
    public void start(Stage stage) throws Exception {

        Pane pane = new Pane();

        scrollPane = new ScrollPane(pane);
        scrollPane.setPrefWidth(1000);
        scrollPane.setPrefHeight(1000);

        final Canvas actionCanvas = new Canvas(); //need to set dyanmically according to image dimensions with scaling set to minimum - meaning the farthest zoom possible
        final GraphicsContext gcAction = actionCanvas.getGraphicsContext2D();
        gcAction.setFill(new Color(0, 0, 0, 1));
        gcAction.fillRect(0, 0, actionCanvas.getWidth(), actionCanvas.getHeight());

        final Canvas backCanvas = new Canvas(); //need to set dyanmically according to image dimensions with scaling set to minimum - meaning the farthest zoom possible
        graphData = new GraphReader().readGraph("graph2.txt");
        GraphDrawer graphDrawer = new GraphDrawer();
        graphDrawer.drawGraph(backCanvas, graphData, scale);
        backCanvas.setCacheHint(CacheHint.SPEED);
        backCanvas.setCache(true);

        pane.setPrefWidth(8000);
        pane.setPrefHeight(8000);

        pane.getChildren().add(backCanvas);
        pane.getChildren().add(actionCanvas);
        actionCanvas.toFront();

        scrollPane.addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double mousePointX = t.getSceneX() + backCanvas.getTranslateX();
                double mousePointY = t.getSceneY() + backCanvas.getTranslateY();
                System.out.println(scrollPane.getHvalue());
//                scrollPane.setHvalue();
//                scrollPane.setVvalue();
                scale = scale + t.getDeltaY() / 200;
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                double newHval = (mousePointX) / (pane.getWidth() - scrollPane.getViewportBounds().getWidth());
                double newVval = (mousePointY) / (pane.getHeight() - scrollPane.getViewportBounds().getHeight());
                System.out.println("newHval: " + newHval + ", newVval: " + newVval);
                scrollPane.setHvalue(newHval);
                scrollPane.setVvalue(newVval);

                graphDrawer.drawGraph(backCanvas, graphData, scale);

            }
        });

        scrollPane.widthProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            actionCanvas.setWidth(n.doubleValue());
            backCanvas.setWidth(n.doubleValue());
            graphDrawer.drawGraph(backCanvas, graphData, scale);
        });

        scrollPane.heightProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            actionCanvas.setHeight(n.doubleValue());
            backCanvas.setHeight(n.doubleValue());
            graphDrawer.drawGraph(backCanvas, graphData, scale);
        });

        scrollPane.hvalueProperty().addListener((ov, n, o) -> {
            actionCanvas.setTranslateX(o.doubleValue() * (pane.getWidth() - scrollPane.getViewportBounds().getWidth()));
            backCanvas.setTranslateX(o.doubleValue() * (pane.getWidth() - scrollPane.getViewportBounds().getWidth()));
            graphDrawer.drawGraph(backCanvas, graphData, scale);
        });

        scrollPane.vvalueProperty().addListener((ov, n, o) -> {
            actionCanvas.setTranslateY(o.doubleValue() * (pane.getHeight() - scrollPane.getViewportBounds().getHeight()));
            backCanvas.setTranslateY(o.doubleValue() * (pane.getHeight() - scrollPane.getViewportBounds().getHeight()));
            graphDrawer.drawGraph(backCanvas, graphData, scale);
        });

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        for (int i = 0; i < 1; i++) {
            final Sprite s = new Car(i);
            sprites.add(s);
        }

        Output output = new Output(new FileOutputStream("file.bin"));
        //writes ticks and events to output file
        Kryo kryo = new Kryo();
        kryo.register(MoveEvent.class);
        kryo.register(TickEvent.class);
        kryo.register(SimulatorEvent.class);
        
        
        eventWriter = new EventWriter(kryo);
        eventReader = new EventReader(kryo);
        writeTicksAndEvents(output);

        final Input input = new Input(new FileInputStream("file.bin"));

        final Timeline timeLine = new Timeline();
        timeLine.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                timeLine.getKeyFrames().clear();
//                addNewMoves(timeLine);
                Collection<SimulatorEvent> tickEvents = eventReader.readNextTickFromInput(input);
                addNewMovesFromEvents(timeLine, tickEvents);
            }
        });
        timeLine.setAutoReverse(false);
        timeLine.setCycleCount(1);
//        addNewMoves(timeLine);
        Collection<SimulatorEvent> tickEvents = eventReader.readNextTickFromInput(input);
        addNewMovesFromEvents(timeLine, tickEvents);
//        timeLine.play();
//        gcAction.save();
        AnimationTimer atimer = new AnimationTimer() {
            long time = System.currentTimeMillis();
            long f = 0;

            @Override
            public void handle(long l) {
                if (System.currentTimeMillis() - time >= 1000) {
                    System.out.println("FPS: " + f);
                    f = 0;
                    time = System.currentTimeMillis();
                }

                f++;
//                gcAction.restore();
//                gcAction.save();
                gcAction.setFill(new Color(1, 1, 1, 1));
                gcAction.clearRect(0, 0, actionCanvas.getWidth(), actionCanvas.getHeight());

                double tx = actionCanvas.getTranslateX();
                double ty = actionCanvas.getTranslateY();
                gcAction.strokeText("tx: " + tx + ", ty: " + ty, 14, 14);

                for (Sprite sprite : sprites) {
                    sprite.setScale(scale);
                    sprite.draw(actionCanvas);
                }

            }
        };

        atimer.start();

        Scene scene = new Scene(scrollPane);

        stage.setScene(scene);

        stage.show();
    }

    /**
     * writes ticks and move events to an output file
     *
     * @param output
     * @throws KryoException
     */
    private void writeTicksAndEvents(Output output) throws KryoException {
        String currEdge = "427182875 1775801775";
        String nextEdge = "";
        Random rand = new Random(System.currentTimeMillis());
        for (int edges = 0; edges < 100000; edges++) {
            Set<String> outgoing = graphData.getEdgesOf(currEdge.split(" ")[1]);
            Object[] setArray = outgoing.toArray();
            nextEdge = (String)setArray[rand.nextInt(outgoing.size())];
            for (int i = 0; i <= 10; i = i + 5) {
                eventWriter.writeTick(output, i);
                int percentage = (int) (((double) i / 10) * 100);
                for (int j = 0; j < sprites.size(); j++) {
                    if (Math.random() > 0) {
                        eventWriter.writeEvent(output, new MoveEvent(j, currEdge, percentage));
                    }
                }
            }
            currEdge = nextEdge;
        }
        output.close();

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

    public void addNewMovesFromEvents(Timeline timeline, Collection events) {
        for (Object event : events) {
            if (event instanceof MoveEvent) {
                MoveEvent movee = (MoveEvent) event;
                Integer who = movee.getId();
                String edge = movee.getEdge();

                Location location = translateToLocation(edge, movee.getPercentage());
                Sprite currSprite = sprites.get(who);
                if (currSprite instanceof Car) {
                    Car currCar = (Car) currSprite;
                    KeyFrame move = currCar.move(location.getX(), location.getY());
                    timeline.getKeyFrames().add(move);
                }
//                System.out.println("added a keyframe");

            }
        }
        if (!events.isEmpty() && timeline.getStatus() != Animation.Status.RUNNING) {
            timeline.play();
        }
    }

    /**
     * assumes that an edge name is "source dest". for example an edge from A to
     * B is named "A B"
     *
     * @param edgeName
     * @param precentage
     * @return
     */
    private Location translateToLocation(String edgeName, Integer precentage) {
        String src = graphData.getEdgeSource(edgeName);
        String target = graphData.getEdgeTarget(edgeName);
        AZVisVertex srcData = (AZVisVertex) graphData.getData(src);
        AZVisVertex targetData = (AZVisVertex) graphData.getData(target);
        double xsub = Math.abs(srcData.getX() - targetData.getX());
        double ysub = Math.abs(srcData.getY() - targetData.getY());
        double totalDistance = Math.sqrt(xsub * xsub + ysub * ysub);
        double distance = totalDistance * (precentage / 100D);
        double angle = Math.atan2(ysub, xsub);

        double newx = Math.abs(srcData.getX() + distance * Math.cos(angle));
        double newy = Math.abs(distance * Math.sin(angle) - srcData.getY());
        if (srcData.getX() > targetData.getX()) {
            newx = srcData.getX() - distance * Math.cos(angle);
        }
        if (srcData.getY() < targetData.getY()) {
            newy = Math.abs(distance * Math.sin(angle) + srcData.getY());
        }

        return new Location(newx, newy);
    }

}
