/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.SimpleScrollableVisualScene;
import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import bgu.dcr.az.vis.player.impl.entities.SpriteBasedEntity;
import java.awt.geom.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class MapVisualScene extends SimpleScrollableVisualScene {

    private static final double MIN_SCALE = 0.01;
    private static final double MAX_SCALE = 10;

    private static double DEFAULT_CONTAINER_WIDTH = 10000;
    private static double DEFAULT_CONTAINER_HEIGHT = 10000;

    public MapVisualScene(int carNum, String mapPath) {
        super(DEFAULT_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);

        MapCanvasLayer back = new MapCanvasLayer(this, mapPath);
        CanvasLayer front = new CanvasLayer(this);

        registerLayer(MapCanvasLayer.class, back, back.getCanvas());
        registerLayer(CanvasLayer.class, front, front.getCanvas());

        Point2D.Double bounds = back.getGraphData().getBounds();
        super.setContainerSize(bounds.x, bounds.y);

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
            addEntity(i, new DefinedSizeSpriteBasedEntity(i, CanvasLayer.class, greenCarImage, DefinedSizeSpriteBasedEntity.SizeParameter.WIDTH, 1.7));
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

}
