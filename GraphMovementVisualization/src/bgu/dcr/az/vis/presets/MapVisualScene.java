/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.Layer;
import bgu.dcr.az.vis.proc.api.VisualScene;
import bgu.dcr.az.vis.proc.impl.CanvasLayer;
import bgu.dcr.az.vis.proc.impl.entities.SpriteBasedEntity;
import java.util.Collection;
import java.util.HashMap;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class MapVisualScene extends ScrollPane implements VisualScene {

    private final HashMap<Long, Layer> layers;
    private final HashMap<Long, SpriteBasedEntity> entities;

    private static final double MIN_SCALE = 0.2;
    private static final double MAX_SCALE = 2;

    public MapVisualScene(int carNum, String mapPath) {
        this.layers = new HashMap<>();
        Pane pane = new Pane();
        pane.setPrefWidth(8000);
        pane.setPrefHeight(8000);
        setContent(pane);
        setPrefWidth(1000);
        setPrefHeight(1000);
        MapCanvasLayer back = new MapCanvasLayer(mapPath);
        CanvasLayer front = new CanvasLayer();
        pane.getChildren().add(back.getCanvas());
        pane.getChildren().add(front.getCanvas());
        front.getCanvas().toFront();

        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double mousePointX = t.getSceneX() + back.getCanvas().getTranslateX();
                double mousePointY = t.getSceneY() + back.getCanvas().getTranslateY();
                System.out.println(getHvalue());
                double scale = back.getScale() + t.getDeltaY() / 200;
//                scrollPane.setHvalue();
//                scrollPane.setVvalue();
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                back.scaleProperty().set(scale);
                front.scaleProperty().set(scale);

                double newHval = (mousePointX) / (pane.getWidth() - getViewportBounds().getWidth());
                double newVval = (mousePointY) / (pane.getHeight() - getViewportBounds().getHeight());
                System.out.println("newHval: " + newHval + ", newVval: " + newVval);
                setHvalue(newHval);
                setVvalue(newVval);

                back.drawGraph();
            }
        });

        widthProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            front.widthProperty().set(n.doubleValue());
            back.widthProperty().set(n.doubleValue());
            back.drawGraph();
        });

        heightProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            front.heightProperty().set(n.doubleValue());
            back.heightProperty().set(n.doubleValue());
            back.drawGraph();
        });

        hvalueProperty().addListener((ov, n, o) -> {
            double transleteX = o.doubleValue() * (pane.getWidth() - getViewportBounds().getWidth());
            front.translateXProperty().set(transleteX);
            back.translateXProperty().set(transleteX);
            back.drawGraph();
        });

        vvalueProperty().addListener((ov, n, o) -> {
            double translateY = o.doubleValue() * (pane.getHeight() - getViewportBounds().getHeight());
            front.translateYProperty().set(translateY);
            back.translateYProperty().set(translateY);
            back.drawGraph();
        });

        setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        layers.put(0l, back);
        layers.put(1l, front);
        Image carImage = new Image(R.class.getResourceAsStream("car.jpg"));
        entities = new HashMap<>();
        for (long i = 0; i < carNum; i++) {
            entities.put(i, new SpriteBasedEntity(i, 1, carImage));
        }
    }

    @Override
    public Layer getLayer(long id) {
        return layers.get(id);
    }

    @Override
    public Collection<? extends Layer> getLayers() {
        Collection<Layer> ls = layers.values();
        return ls;
    }

    @Override
    public Entity getEntity(long id) {
        return entities.get(id);
    }

    @Override
    public Collection<? extends Entity> getEntities() {
        Collection<SpriteBasedEntity> ents = entities.values();
        return ents;
    }

}
