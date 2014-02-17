/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmovementvisualization;

import graphicx.sprite.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import graphicx.sprite.Car;
import graphmovementvisualization.api.SimulatorEvent;
import graphmovementvisualization.api.SimulatorTick;
import graphmovementvisualization.impl.GraphData;
import graphmovementvisualization.impl.MoveEvent;
import graphmovementvisualization.impl.TickEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
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
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
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
    private Kryo kryo;
    private GraphData graphData = new GraphData();
    private ScrollPane scrollPane;

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
        graphData = getGraphData();
        drawBackgroundFromGraph(backCanvas, graphData);
        backCanvas.setCacheHint(CacheHint.SPEED);
        backCanvas.setCache(true);

        scrollPane.widthProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            actionCanvas.setWidth(n.doubleValue());
            backCanvas.setWidth(n.doubleValue());
            drawBackgroundFromGraph(backCanvas, graphData);
        });

        scrollPane.heightProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            actionCanvas.setHeight(n.doubleValue());
            backCanvas.setHeight(n.doubleValue());
            drawBackgroundFromGraph(backCanvas, graphData);
        });

        scrollPane.hvalueProperty().addListener((ov, n, o) -> {
            actionCanvas.setTranslateX(o.doubleValue() * (pane.getWidth() - scrollPane.getViewportBounds().getWidth()));
            backCanvas.setTranslateX(o.doubleValue() * (pane.getWidth() - scrollPane.getViewportBounds().getWidth()));
            drawBackgroundFromGraph(backCanvas, graphData);
        });

        scrollPane.vvalueProperty().addListener((ov, n, o) -> {
            actionCanvas.setTranslateY(o.doubleValue() * (pane.getHeight() - scrollPane.getViewportBounds().getHeight()));
            backCanvas.setTranslateY(o.doubleValue() * (pane.getHeight() - scrollPane.getViewportBounds().getHeight()));
            drawBackgroundFromGraph(backCanvas, graphData);
        });

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        pane.setPrefWidth(8000);
        pane.setPrefHeight(8000);

//        .bind(scrollPane.getContent().translateXProperty());
        pane.getChildren().add(backCanvas);
        pane.getChildren().add(actionCanvas);
        actionCanvas.toFront();

//        actionCanvas.setCacheHint(CacheHint.SPEED);
//        actionCanvas.setCache(true);
        for (int i = 0; i < 1; i++) {
            final Sprite s = new Car(i);
            sprites.add(s);
        }

        kryo = new Kryo();
        Output output = new Output(new FileOutputStream("file.bin"));
        //writes ticks and events to output file
        writeTicksAndEvents(output);

        final Input input = new Input(new FileInputStream("file.bin"));

        final Timeline timeLine = new Timeline();
        timeLine.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                timeLine.getKeyFrames().clear();
//                addNewMoves(timeLine);
                Collection<SimulatorEvent> tickEvents = readTickFromInput(input);
                addNewMovesFromEvents(timeLine, tickEvents);
            }
        });
        timeLine.setAutoReverse(false);
        timeLine.setCycleCount(1);
//        addNewMoves(timeLine);
        Collection<SimulatorEvent> tickEvents = readTickFromInput(input);
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

        for (int i = 0; i <= 10; i = i + 5) {
            kryo.writeClassAndObject(output, new TickEvent(i));
            int percentage = (int) (((double) i / 10) * 100);
//            System.out.println("percentage = " + percentage);
            for (int j = 0; j < sprites.size(); j++) {
                if (Math.random() > 0) {
                    kryo.writeClassAndObject(output, new MoveEvent(j, "427182875 1775801775", percentage));
                }

            }
        }
        for (int i = 0; i <= 10; i = i + 2) {
            kryo.writeClassAndObject(output, new TickEvent(i));
            int percentage = (int) (((double) i / 10) * 100);
//            System.out.println("percentage = " + percentage);
            for (int j = 0; j < sprites.size(); j++) {
                if (Math.random() > 0) {
                    kryo.writeClassAndObject(output, new MoveEvent(j, "1775801775 427182876", percentage));
                }
            }
        }
        for (int i = 0; i <= 10; i = i + 2) {
            kryo.writeClassAndObject(output, new TickEvent(i));
            int percentage = (int) (((double) i / 10) * 100);
            for (int j = 0; j < sprites.size(); j++) {
                if (Math.random() > 0) {
                    kryo.writeClassAndObject(output, new MoveEvent(j, "427182876 427182878", percentage));
                }
            }
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
                    Car currCar = (Car)currSprite;
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

    private GraphData getGraphData() {
        try {
            Scanner in;
            in = new Scanner(new File("graph2.txt"));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                Scanner lineBreaker = new Scanner(line);
                String nextToken = lineBreaker.next();
                if (nextToken.equals("V")) {
                    String name = lineBreaker.next();
                    nextToken = lineBreaker.next();
                    Collection<Double> ints = parseVertex(lineBreaker, nextToken);
                    Iterator<Double> iterator = ints.iterator();
                    AZVisVertex vertexData = new AZVisVertex(name, iterator.next(), iterator.next());
                    graphData.addVertex(name, vertexData);
                } else if (nextToken.equals("E")) {
                    String from = lineBreaker.next();
                    String to = lineBreaker.next();
                    graphData.addEdge(from + " " + to, from, to, null);
                } else {
                    System.out.println("unsupported!");

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GraphMovementVisualization.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return graphData;
    }

    private Collection<Double> parseVertex(Scanner lineBreaker, String nextToken) {
        LinkedList<Double> ints = new LinkedList<>();
        while (lineBreaker.hasNext()) {
            if (nextToken.charAt(0) == '[') {
                nextToken = nextToken.substring(1);
                while (!(nextToken.charAt(nextToken.length() - 1) == ']')) {
                    ints.add(Double.parseDouble(nextToken));
                    nextToken = lineBreaker.next();
                }
                ints.add(Double.parseDouble(nextToken.substring(0, nextToken.length() - 1)));
            }
        }
        return ints;
    }

    private void drawBackgroundFromGraph(Canvas canvas, GraphData graphData) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        gc.setFill(new Color(0, 0, 0, 1));
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.strokeText("tx: " + tx + ", ty: " + ty, 14, canvas.getHeight() - 14);
        for (String vertexName : graphData.getVertexSet()) {
            AZVisVertex vertex = (AZVisVertex) graphData.getData(vertexName);
            gc.strokeRect(vertex.getX() - tx, vertex.getY() - ty, 5, 5);

        }
        for (String edgeName : graphData.getEdgeSet()) {
            AZVisVertex source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
            AZVisVertex target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
            gc.strokeLine(source.getX() - tx, source.getY() - ty, target.getX() - tx, target.getY() - ty);
        }
    }

    private Collection<SimulatorEvent> readTickFromInput(Input input) {
        LinkedList tickEvents = new LinkedList();
        try {
            if (input.available() != 0) {
                Object readObject = kryo.readClassAndObject(input);
                int lastPosition = input.position();
                if (readObject instanceof TickEvent) {
                    readObject = kryo.readClassAndObject(input);
                    while (!(readObject instanceof TickEvent)) {
                        Object event = readObject;
                        tickEvents.add(event);
                        lastPosition = input.position();
                        if (input.available() == 0) {
                            break;
                        }
                        readObject = kryo.readClassAndObject(input);
                    }
                }
                input.setPosition(lastPosition);
            }
        } catch (IOException ex) {
            Logger.getLogger(GraphMovementVisualization.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickEvents;
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
