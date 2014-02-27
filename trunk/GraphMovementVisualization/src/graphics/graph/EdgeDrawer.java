/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import data.graph.impl.AZVisVertex;
import data.graph.impl.GraphData;
import data.graph.impl.GraphPolygon;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    private final int MAIN_THICKNESS = 12;
    private final int INNER_THICKNESS = 8;
    private final EdgeDescriptor defaultDescriptor;
    private final HashMap<String, EdgeDescriptor> descriptors;

    public EdgeDrawer() {
        descriptors = new HashMap<>();
        ImagePattern roadImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("roadTexture.jpg")), 0, 0, 100, 100, false);

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

    public void draw(Canvas canvas, GraphData graphData, Collection<String> edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(edges.iterator().next());
        EdgeDescriptor ed = descriptors.get(edgeData.get(ROAD_KEY));
        if (ed == null) {
            ed = defaultDescriptor;
        }

        gc.save();
        gc.beginPath();
        for (String edgeName : edges) {
            AZVisVertex source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
            AZVisVertex target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));

            gc.setLineCap(ed.getOuterStroke().getLineCap());
            gc.setLineJoin(ed.getOuterStroke().getLineJoin());
            gc.setLineWidth(ed.getOuterStroke().getWidth() * scale);
            gc.setStroke(ed.getOuterStroke().getPaint());

            gc.setLineCap(ed.getInnerStroke().getLineCap());
            gc.setLineJoin(ed.getInnerStroke().getLineJoin());
            gc.setLineWidth(ed.getInnerStroke().getWidth() * scale);
            gc.setStroke(ed.getInnerStroke().getPaint());

            gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
            gc.lineTo((target.getX() - tx) * scale, (target.getY() - ty) * scale);
        }
        gc.stroke();
        gc.restore();

    }

    public void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();
        gc.save();
        gc.setFill(Color.RED);
        gc.beginPath();
        Iterator<String> it = polygon.getNodes().iterator();
        String node = it.next();
        AZVisVertex source = (AZVisVertex) graphData.getData(node);

//        gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
        double[] xs = new double[polygon.getNodes().size()];
        double[] ys = new double[polygon.getNodes().size()];

        xs[0] = (source.getX() - tx) * scale;
        ys[0] = (source.getY() - ty) * scale;
        int i = 1;
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
//            gc.lineTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);

            xs[i] = (source.getX() - tx) * scale;
            ys[i] = (source.getY() - ty) * scale;
            i++;

        }

//        gc.fill();
        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();

    }

}
