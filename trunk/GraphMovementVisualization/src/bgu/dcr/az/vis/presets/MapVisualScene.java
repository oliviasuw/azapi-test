/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.SimpleScrollableVisualScene;
import bgu.dcr.az.vis.player.impl.entities.SpriteBasedEntity;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class MapVisualScene extends SimpleScrollableVisualScene {

    private static final double MIN_SCALE = 0.2;
    private static final double MAX_SCALE = 2;

    private static final double CONTAINER_WIDTH = 8000;
    private static final double CONTAINER_HEIGHT = 8000;

    public MapVisualScene(int carNum, String mapPath) {
        super(CONTAINER_WIDTH, CONTAINER_HEIGHT);
        MapCanvasLayer back = new MapCanvasLayer(this, mapPath);
        CanvasLayer front = new CanvasLayer(this);

        registerLayer(MapCanvasLayer.class, back, back.getCanvas());
        registerLayer(CanvasLayer.class, front, front.getCanvas());

        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double scale = back.getScale() + t.getDeltaY() / 500;
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                scaleProperty().set(scale);

//                double mousePointX = t.getSceneX() + back.getCanvas().getTranslateX();
//                double mousePointY = t.getSceneY() + back.getCanvas().getTranslateY();
//
//                double newHval = (mousePointX) / (CONTAINER_WIDTH - getViewportBounds().getWidth());
//                double newVval = (mousePointY) / (CONTAINER_HEIGHT - getViewportBounds().getHeight());
//                setHvalue(newHval);
//                setVvalue(newVval);

                back.drawGraph();
                t.consume();
            }
        });

        widthProperty().addListener((ov, o, n) -> back.drawGraph());
        heightProperty().addListener((ov, o, n) -> back.drawGraph());
        hvalueProperty().addListener((ov, n, o) -> back.drawGraph());
        vvalueProperty().addListener((ov, n, o) -> back.drawGraph());

        Image greenCarImage = new Image(R.class.getResourceAsStream("car-green.jpg"));
        for (long i = 0; i < carNum; i++) {
            addEntity(i, new SpriteBasedEntity(i, CanvasLayer.class, greenCarImage));
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

}
