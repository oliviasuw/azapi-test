/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import bgu.dcr.az.vis.presets.map.MapVisualScene;
import com.bbn.openmap.util.quadtree.QuadTree;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class EdgeDrawer {

    public static final String ROAD_KEY = "highway";
    public static final String GRASS_KEY = "landuse";
//    private final int MAIN_THICKNESS = 12;
//    private final int INNER_THICKNESS = 8;
    private final double LANE_WIDTH = 3.6; //in meters
    private final EdgeDescriptor defaultDescriptor;
    private final HashMap<String, EdgeDescriptor> descriptors;

    public EdgeDrawer() {
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

    public boolean canDraw(String edgeName, GraphData graphData) {
        HashMap<String, String> data = (HashMap<String, String>) graphData.getData(edgeName);
        return data.get(ROAD_KEY) != null;
    }

    public void draw(Canvas canvas, GraphData graphData, String edgeName, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        AZVisVertex source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
        AZVisVertex target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
        HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(edgeName);
        EdgeDescriptor ed = descriptors.get(edgeData.get(ROAD_KEY));

        if (ed == null) {
            ed = defaultDescriptor;
        }

        double pixelLaneWidth = LANE_WIDTH;

        gc.save();

        gc.setLineCap(ed.getOuterStroke().getLineCap());
        gc.setLineJoin(ed.getOuterStroke().getLineJoin());
        gc.setLineWidth(pixelLaneWidth * scale);
        gc.setStroke(ed.getOuterStroke().getPaint());
        gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);

        gc.setLineCap(ed.getInnerStroke().getLineCap());
        gc.setLineJoin(ed.getInnerStroke().getLineJoin());
        gc.setLineWidth(pixelLaneWidth * scale);
        gc.setStroke(ed.getInnerStroke().getPaint());
        gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);

        gc.restore();

    }

    public void draw(Canvas canvas, GraphData graphData, QuadTree edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();
//        double factor = 1/scale;
        double epsilonW = graphData.getMaxEdgeWidth()/2;
        double epsilonH = graphData.getMaxEdgeHeight()/2;
//        System.out.println("h: " + epsilonH + " w: "+epsilonW);
        Vector edgevec = edges.get((float) ((ty + canvas.getHeight() + epsilonH)*scale), (float) ((tx-epsilonW)*scale), (float) ((ty-epsilonH)*scale), (float) ((tx + canvas.getWidth()+epsilonW)*scale));

        if (edgevec.size() > 0) {
            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData((String) edgevec.get(0));
            EdgeDescriptor ed = descriptors.get(edgeData.get(ROAD_KEY));
            if (ed == null) {
                ed = defaultDescriptor;
            }

            double pixelLaneWidth = LANE_WIDTH;

            gc.save();
            gc.beginPath();
            for (Object edgeObj : edgevec) {
                String edgeName = (String) edgeObj;
                AZVisVertex source = null;
                AZVisVertex target = null;
                try {
                    source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
                    target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
                } catch (Exception e) {
                    System.out.println("problem with drawing! cant find some node.");
                    continue;
                }
                gc.setLineCap(ed.getOuterStroke().getLineCap());
                gc.setLineJoin(ed.getOuterStroke().getLineJoin());
                gc.setLineWidth(ed.getOuterStroke().getLanes() * pixelLaneWidth * scale);
                gc.setStroke(ed.getOuterStroke().getPaint());

//            gc.setLineCap(ed.getInnerStroke().getLineCap());
//            gc.setLineJoin(ed.getInnerStroke().getLineJoin());
//            gc.setLineWidth(pixelLaneWidth * scale);
//            gc.setStroke(ed.getInnerStroke().getPaint());
                gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
                gc.lineTo((target.getX() - tx) * scale, (target.getY() - ty) * scale);
            }
            gc.stroke();
            gc.restore();
        }
    }
    
    public void draw(Canvas canvas, GraphData graphData, Collection edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();
        
        if (edges.size() > 0) {
            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData((String) edges.iterator().next());
            EdgeDescriptor ed = descriptors.get(edgeData.get(ROAD_KEY));
            if (ed == null) {
                ed = defaultDescriptor;
            }

            double pixelLaneWidth = LANE_WIDTH;

            gc.save();
            gc.beginPath();
            for (Object edgeObj : edges) {
                String edgeName = (String) edgeObj;
                AZVisVertex source = null;
                AZVisVertex target = null;
                try {
                    source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
                    target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
                } catch (Exception e) {
                    System.out.println("problem with drawing! cant find some node.");
                    continue;
                }
                gc.setLineCap(ed.getOuterStroke().getLineCap());
                gc.setLineJoin(ed.getOuterStroke().getLineJoin());
                gc.setLineWidth(ed.getOuterStroke().getLanes() * pixelLaneWidth * scale);
                gc.setStroke(ed.getOuterStroke().getPaint());

//            gc.setLineCap(ed.getInnerStroke().getLineCap());
//            gc.setLineJoin(ed.getInnerStroke().getLineJoin());
//            gc.setLineWidth(pixelLaneWidth * scale);
//            gc.setStroke(ed.getInnerStroke().getPaint());
                gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
                gc.lineTo((target.getX() - tx) * scale, (target.getY() - ty) * scale);
            }
            gc.stroke();
            gc.restore();
        }
    }

}
