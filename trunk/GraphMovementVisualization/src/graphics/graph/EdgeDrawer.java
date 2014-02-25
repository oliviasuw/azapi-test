/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import data.graph.impl.AZVisVertex;
import data.graph.impl.GraphData;
import java.util.HashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Shl
 */
public class EdgeDrawer {

    public static final String ROAD_KEY = "highway";
    private final int MAIN_THICKNESS = 12;
    private final int INNER_THICKNESS = 8;
    private final EdgeDescriptor defaultDescriptor;
    private final HashMap<String, EdgeDescriptor> descriptors;

    public EdgeDrawer() {
        descriptors = new HashMap<>();
        ImagePattern roadImagePattern = new ImagePattern(new Image("roadTexture.jpg"), 0, 0, 100, 100, false);

        descriptors.put("primary", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("secondary", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY)));

        descriptors.put("tertiary", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        roadImagePattern),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        roadImagePattern)));
        
//        descriptors.put("tertiary", new EdgeDescriptor(
//                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLACK),
//                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
//                        Color.BLUE)));

        descriptors.put("trunk", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.MAGENTA)));
        descriptors.put("residential", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.YELLOW)));
        descriptors.put("primary_link", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.ORANGE)));
        descriptors.put("service", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("road", new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.WHITE)));
        descriptors.put("pedestrian", new EdgeDescriptor(
                new EdgeStroke(3, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GRAY)));
        descriptors.put("living_street", new EdgeDescriptor(
                new EdgeStroke(3, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.CYAN)));
        descriptors.put("footway", new EdgeDescriptor(
                new EdgeStroke(3, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.BLACK),
                new EdgeStroke(1, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.GREEN)));

        defaultDescriptor = new EdgeDescriptor(
                new EdgeStroke(MAIN_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
                        Color.ORANGE),
                new EdgeStroke(INNER_THICKNESS, StrokeLineCap.ROUND, StrokeLineJoin.ROUND,
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

        gc.save();

        gc.setLineCap(ed.getOuterStroke().getLineCap());
        gc.setLineJoin(ed.getOuterStroke().getLineJoin());
        gc.setLineWidth(ed.getOuterStroke().getWidth() * scale);
        gc.setStroke(ed.getOuterStroke().getPaint());
        gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);

        gc.setLineCap(ed.getInnerStroke().getLineCap());
        gc.setLineJoin(ed.getInnerStroke().getLineJoin());
        gc.setLineWidth(ed.getInnerStroke().getWidth() * scale);
        gc.setStroke(ed.getInnerStroke().getPaint());
        gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);

        gc.restore();

    }

}
