/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmovementvisualization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Shl
 */
public class GraphMovementVisualization extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        final int width = 800;
        final int height = 800;
        final Canvas canvas = new Canvas(width, height); //need to set dyanmically according to image dimensions with scaling set to minimum - meaning the farthest zoom possible
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final Random r = new Random();

        final ArrayList<Sprite> sprites = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Sprite sprite = new Sprite();
            sprites.add(sprite);

            final DoubleProperty x = new SimpleDoubleProperty();
            final DoubleProperty y = new SimpleDoubleProperty();

            sprite.setX(x);
            sprite.setY(y);

            final Timeline timeLine = new Timeline();
            timeLine.setCycleCount(Timeline.INDEFINITE);

            //maybe create a timeline for each movement event and play it like this :
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.millis(500),
                            new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent t) {
                                    x.setValue(0);
                                    y.setValue(0);
                                }
                            },
                            new KeyValue(x, r.nextInt(width)), new KeyValue(y, r.nextInt(height)))
            );
            timeLine.play();
        }
        new AnimationTimer() {
            @Override
            public void handle(long l
            ) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                for (Sprite sprite : sprites) {
                    gc.drawImage(sprite.getImage(), sprite.getX().doubleValue(), sprite.getY().doubleValue());
                }
            }
        }
                .start();

        Kryo kryo = new Kryo();
        Output output = new Output(new FileOutputStream("file.bin"));

        String someObject = "booyakash";
        String someObject2 = "kuwabangah";

        kryo.writeClassAndObject(output, someObject);

        kryo.writeClassAndObject(output, someObject2);

        output.close();

        Input input = new Input(new FileInputStream("file.bin"));
        Object object = kryo.readClassAndObject(input);
        if (object instanceof String) {
            System.out.println(object);
        }

        System.out.println(input.available());
        System.out.println(input.available());

        Object object2 = kryo.readClassAndObject(input);

        System.out.println(object2);

        System.out.println(input.available());
        input.close();

//        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        HBox box = new HBox();

        box.getChildren()
                .add(canvas);

        Scene scene = new Scene(box);

        stage.setScene(scene);

        stage.show();
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
