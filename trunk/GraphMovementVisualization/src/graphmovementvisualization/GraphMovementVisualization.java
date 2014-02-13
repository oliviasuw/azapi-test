/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmovementvisualization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import graphmovementvisualization.api.SimulatorEvent;
import graphmovementvisualization.api.SimulatorTick;
import graphmovementvisualization.impl.AZVisGraph;
import graphmovementvisualization.impl.SimpleSimulatorEvent;
import graphmovementvisualization.impl.SimpleSimulatorTick;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import sun.security.provider.certpath.Vertex;

/**
 *
 * @author Shl
 */
public class GraphMovementVisualization extends Application {

    private final ArrayList<Sprite> sprites = new ArrayList<>();

    final int width = 1000;
    final int height = 1000;
    final Random r = new Random();
    private Kryo kryo;

    @Override
    public void start(Stage stage) throws Exception {

        final Canvas actionCanvas = new Canvas(width, height); //need to set dyanmically according to image dimensions with scaling set to minimum - meaning the farthest zoom possible
        final GraphicsContext gcAction = actionCanvas.getGraphicsContext2D();
        gcAction.setFill(new Color(0, 0, 0, 1));
        gcAction.fillRect(0, 0, width, height);

        final Canvas backCanvas = new Canvas(width, height); //need to set dyanmically according to image dimensions with scaling set to minimum - meaning the farthest zoom possible
        final GraphicsContext gcBack = backCanvas.getGraphicsContext2D();
        AZVisGraph graph = getGraph();
        drawBackgroundFromGraph(gcBack, graph);

        for (int i = 0; i < 1; i++) {
            final Sprite s = new Sprite(i);
            sprites.add(s);
        }

        kryo = new Kryo();
        Output output = new Output(new FileOutputStream("file.bin"));

        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                kryo.writeClassAndObject(output, new SimpleSimulatorTick());
            } else {
                for (Sprite sprite : sprites) {
                    LinkedList<Integer> list = new LinkedList<>();
                    list.add(sprite.getIndex());
                    list.add(r.nextInt(width));
                    list.add(r.nextInt(height));
                    SimpleSimulatorEvent event = new SimpleSimulatorEvent("MOVE", list);
                    kryo.writeClassAndObject(output, event);
                }
            }
        }
        output.close();

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

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                gcAction.clearRect(0, 0, backCanvas.getWidth(), backCanvas.getHeight());
                for (Sprite sprite : sprites) {
                    gcAction.drawImage(sprite.getImage(), sprite.getLocation().getX().doubleValue(), sprite.getLocation().getY().doubleValue());
                }
            }
        }.start();

        Pane pane = new Pane();

        pane.getChildren().add(backCanvas);
        pane.getChildren().add(actionCanvas);
        actionCanvas.toFront();

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setPrefWidth(900);
        scrollPane.setPrefHeight(900);

        Scene scene = new Scene(scrollPane);

        stage.setScene(scene);

        stage.show();

//        Input input = new Input(new FileInputStream("file.bin"));
//        while (input.available() != 0) {
//            boolean currentTick = true;
//            while (currentTick && input.available() != 0) {
//                final Object object = kryo.readClassAndObject(input);
//                new AnimationTimer() {
//                    @Override
//                    public void handle(long l) {
//                        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                        if (object instanceof SimulatorEvent) {
//                            SimulatorEvent event = (SimulatorEvent) object;
//                            if (event.getName().equals("MOVE")) {
//                                Collection<? extends Object> parameters = event.getParameters();
//                                Iterator<? extends Object> iterator = parameters.iterator();
//                                Integer who = (Integer) iterator.next();
//                                Integer x = (Integer) iterator.next();
//                                Integer y = (Integer) iterator.next();
//                                sprites.get(who).move(x, y);
//                            }
//                        }
//                    }
//                }.start();
//
//                if (object instanceof SimulatorTick) {
//                    currentTick = false;
//                    boolean notFinished = true;
//                    while (notFinished) {
//                        notFinished = false;
//                        for (Sprite sprite : sprites) {
//                            gc.drawImage(sprite.getImage(), sprite.getLocation().getX().doubleValue(), sprite.getLocation().getY().doubleValue());
//                            if (sprite.hasTimelineInstructions()) {
//                                notFinished = true;
//                            }
//                        }
//                    }
//                    System.out.println("finished tick!");
//                }
//            }
//        }
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

    public void addNewMoves(Timeline timeline) {
        for (Sprite sprite : sprites) {
            KeyFrame move = sprite.move(r.nextInt(width), r.nextInt(height));
            timeline.getKeyFrames().add(move);
        }
        timeline.play();
    }

    public void addNewMovesFromEvents(Timeline timeline, Collection<SimulatorEvent> events) {
        for (SimulatorEvent event : events) {
            Collection<? extends Object> parameters = event.getParameters();
            Iterator<? extends Object> paramsIterator = parameters.iterator();
            Integer who = (Integer) paramsIterator.next();
            Integer x = (Integer) paramsIterator.next();
            Integer y = (Integer) paramsIterator.next();
            KeyFrame move = sprites.get(who).move(r.nextInt(width), r.nextInt(height));
            timeline.getKeyFrames().add(move);
        }
        timeline.play();
    }

    private AZVisGraph getGraph() {
        AZVisGraph graph = new AZVisGraph();
        try {
            Scanner in;
            in = new Scanner(new File("graph.txt"));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                Scanner lineBreaker = new Scanner(line);
                String nextToken = lineBreaker.next();
                if (nextToken.equals("V")) {
                    String name = lineBreaker.next();
                    nextToken = lineBreaker.next();
                    Collection<Integer> ints = parseVertex(lineBreaker, nextToken);
                    Iterator<Integer> iterator = ints.iterator();
                    graph.addVertex(new AZVisVertex(name, iterator.next(), iterator.next()));
                } else if (nextToken.equals("E")) {
                    String from = lineBreaker.next();
                    String to = lineBreaker.next();
                    graph.addEdgeByNames(from, to);
                } else {
                    System.out.println("unsupported!");

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GraphMovementVisualization.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return graph;
    }

    private Collection<Integer> parseVertex(Scanner lineBreaker, String nextToken) {
        LinkedList<Integer> ints = new LinkedList<>();
        while (lineBreaker.hasNext()) {
            if (nextToken.charAt(0) == '[') {
                nextToken = nextToken.substring(1);
                while (!(nextToken.charAt(nextToken.length() - 1) == ']')) {
                    ints.add(Integer.parseInt(nextToken));
                    nextToken = lineBreaker.next();
                }
                ints.add(Integer.parseInt(nextToken.substring(0, nextToken.length() - 1)));
            }
        }
        return ints;
    }

    private void drawBackgroundFromGraph(GraphicsContext gc, AZVisGraph graph) {
        for (AZVisVertex vertex : graph.vertexSet()) {
            gc.strokeRect(vertex.getX(), vertex.getY(), 5, 5);

        }
        for (DefaultEdge edge : graph.edgeSet()) {
            AZVisVertex source = graph.getEdgeSource(edge);
            AZVisVertex target = graph.getEdgeTarget(edge);
            gc.strokeLine(source.getX(), source.getY(), target.getX(), target.getY());
        }
    }

    private Collection<SimulatorEvent> readTickFromInput(Input input) {
        LinkedList<SimulatorEvent> tickEvents = new LinkedList<>();
        try {
            if (input.available() != 0) {
                Object readObject = kryo.readClassAndObject(input);
                int lastPosition = input.position();
                if (readObject instanceof SimulatorTick) {
                    readObject = kryo.readClassAndObject(input);
                    while (readObject instanceof SimulatorEvent) {
                        SimulatorEvent event = (SimulatorEvent)readObject;
                        if (event.getName().equals("MOVE")) {
                            tickEvents.add(event);
                        }
                        lastPosition = input.position();
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

}
