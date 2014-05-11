/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.tools.Location;
import bgu.dcr.az.vis.tools.StringPair;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeDescriptor;
import graphics.graph.EdgeStroke;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class GraphDrawer extends GroupDrawer {

    private final GraphData graphData;
    private Location viewPortLocation;
    private double viewPortScale;

    public GraphDrawer(DrawerInterface drawer, GraphData graphData) {
        super(drawer);
        this.graphData = graphData;
        this.viewPortLocation = new Location();
        this.viewPortScale = 1;

    }

    @Override
    public void _draw(String group, String subgroup) {

        GroupBoundingQuery boundingQuery = drawer.getQuery();
        double scale = drawer.getScale();
        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData("GRAPH", CanvasLayer.class);
        Canvas canvas = canvasLayer.getCanvas();

        double viewPortWidth = drawer.getViewPortWidth();
        double viewPortHeight = drawer.getViewPortHeight();

        double epsilonH = boundingQuery.getEpsilon(group, subgroup)[1];
        double epsilonW = boundingQuery.getEpsilon(group, subgroup)[0];

        if (subgroup.contains("EDGES")) {
            Collection edges = boundingQuery.get(group, subgroup, drawer.getViewPortLocation().getX() - epsilonW * scale, drawer.getViewPortLocation().getX() + viewPortWidth + epsilonW * scale, drawer.getViewPortLocation().getY() + viewPortHeight + epsilonH * scale, drawer.getViewPortLocation().getY() - epsilonH * scale);
            draw(canvas, graphData, edges, scale);
        }

        if (subgroup.contains("POLYGONS")) {
            Vector polys = (Vector) boundingQuery.get(group, subgroup, drawer.getViewPortLocation().getX() - epsilonW * scale, drawer.getViewPortLocation().getX() + viewPortWidth + epsilonW * scale, drawer.getViewPortLocation().getY() + viewPortHeight + epsilonH * scale, drawer.getViewPortLocation().getY() - epsilonH * scale);
            for (Iterator it = polys.iterator(); it.hasNext();) {
                Object obj = it.next();
                GraphPolygon polygon = (GraphPolygon) obj;
                draw(canvas, graphData, polygon, scale);
            }
        }

    }

    //cancel scale! with viewportsize no need for it! its already included
    public void draw(Canvas canvas, GraphData graphData, Collection edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
//        double tx = canvas.getTranslateX();
//        double ty = canvas.getTranslateY();
        double tx = drawer.getViewPortLocation().getX();
        double ty = drawer.getViewPortLocation().getY();

        EdgesMetaData edgeMeta = (EdgesMetaData) drawer.getQuery().getMetaData("GRAPH", EdgesMetaData.class);

        if (edges.size() > 0) {
            //temporary hack (Edge)
            String name = ((Edge) edges.iterator().next()).getId();
            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(name);
            EdgeDescriptor ed = edgeMeta.getDescriptors().get(edgeData.get(edgeMeta.ROAD_KEY()));
            if (ed == null) {
                ed = edgeMeta.getDefaultDescriptor();
            }

            double pixelLaneWidth = edgeMeta.LANE_WIDTH();

            gc.save();
            gc.beginPath();
            for (Object edgeObj : edges) {
//                String edgeName = (String) edgeObj;
                //temporary hack:
                String edgeName = ((Edge) edgeObj).getId();

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

    private void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale) {

        PolygonMetaData metaData = (PolygonMetaData) drawer.getQuery().getMetaData("GRAPH", PolygonMetaData.class);

        GraphicsContext gc = canvas.getGraphicsContext2D();
//        double tx = canvas.getTranslateX();
//        double ty = canvas.getTranslateY();
        double tx = drawer.getViewPortLocation().getX();
        double ty = drawer.getViewPortLocation().getY();
        gc.save();
        Iterator<String> it = polygon.getNodes().iterator();
        String node = it.next();
        AZVisVertex source = (AZVisVertex) graphData.getData(node);

        //if want to extend to more params per polygon, need to change this
        Map.Entry<String, String> entry = polygon.getParams().entrySet().iterator().next();

        StringPair keyVal = new StringPair(entry.getKey(), entry.getValue());
        Paint get = metaData.getKeyTocolors().get(keyVal);
        if (get == null) {
            System.out.println("unsupported polygon " + keyVal.getFirst() + "=" + keyVal.getSecond());
            get = metaData.getDefaultPaint();
        }
        gc.setFill(get);
        gc.beginPath();

        gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
        int i = 1;
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
            gc.lineTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
        }
        gc.closePath();
        gc.fill();
//        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();
    }

}
