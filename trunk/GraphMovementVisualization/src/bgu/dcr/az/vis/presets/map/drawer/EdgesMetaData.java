/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.presets.map.drawer;

import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeDescriptor;
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
    
    private final double LANE_WIDTH = 3.6; //in meters
    private HashMap<String, EdgeDescriptor> descriptors;
    private EdgeDescriptor defaultDescriptor; 

    public EdgesMetaData() {
        init();
    }

    public HashMap<String, EdgeDescriptor> getDescriptors() {
        return descriptors;
    }

    public EdgeDescriptor getDefaultDescriptor() {
        return defaultDescriptor;
    }

    public double LANE_WIDTH() {
        return LANE_WIDTH;
    }
    
    public void init() {
        descriptors = new HashMap<>();
        ImagePattern roadImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("roadTexture.jpg")), 0, 0, 100, 100, false);

        descriptors.put("primary", new EdgeDescriptor(
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("secondary", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY)));

        descriptors.put("tertiary", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        roadImagePattern),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        roadImagePattern)));

//        descriptors.put("tertiary", new EdgeDescriptor(
//                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLACK),
//                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLUE)));
        descriptors.put("trunk", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.MAGENTA)));
        descriptors.put("residential", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.YELLOW)));
        descriptors.put("primary_link", new EdgeDescriptor(
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.ORANGE)));
        descriptors.put("service", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("road", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("pedestrian", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY)));
        descriptors.put("living_street", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.CYAN)));
        descriptors.put("footway", new EdgeDescriptor(
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GREEN)));
        
         defaultDescriptor = new EdgeDescriptor(
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.ORANGE),
                new EdgeStroke(2, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK));
        
    }
    
    
        
}
