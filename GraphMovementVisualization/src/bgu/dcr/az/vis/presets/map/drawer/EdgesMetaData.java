/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeStroke;
import java.util.HashMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import resources.img.R;

/**
 *
 * @author Shl
 */
public final class EdgesMetaData {

    public static final String ROAD_KEY = "highway";
    private final double LANE_WIDTH = 3.6; //in meters
    private HashMap<String, EdgeStroke> strokes;
    private EdgeStroke defaultStroke;

    public EdgesMetaData() {
        init();
    }

    public EdgeStroke getStroke(String edgeTag) {
        return strokes.get(edgeTag);
    }

    public EdgeStroke getDefaultStroke() {
        return defaultStroke;
    }

    public double LANE_WIDTH() {
        return LANE_WIDTH;
    }

    public String ROAD_KEY() {
        return ROAD_KEY;
    }

    public void init() {
        strokes = new HashMap<>();
        ImagePattern roadImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("roadTexture.jpg")), 0, 0, 100, 100, false);

        strokes.put("primary",
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.rgb(220, 158, 158)));
        strokes.put("secondary",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.rgb(248, 214, 170)));

        strokes.put("tertiary",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.rgb(248, 248, 186)));

//        descriptors.put("tertiary", new EdgeDescriptor(
//                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLACK),
//                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLUE)));
        strokes.put("trunk",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.rgb(148, 212, 148)));
        strokes.put("residential",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE));
        strokes.put("primary_link",
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.ORANGE));
        strokes.put("service",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE));
        strokes.put("road",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE));
        strokes.put("pedestrian",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY));
        strokes.put("living_street",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.CORNFLOWERBLUE));
        strokes.put("footway",
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BURLYWOOD));

        strokes.put("motorway",
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.rgb(137, 164, 202)));

        defaultStroke
                = new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY);

    }

}
