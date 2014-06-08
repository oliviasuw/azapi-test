/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.presets.map.GroupScale;
import bgu.dcr.az.vis.tools.Convertor;
import bgu.dcr.az.vis.tools.Location;
import bgu.dcr.az.vis.tools.StringPair;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeStroke;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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

        double wXs = Convertor.viewToWorld(drawer.getViewPortLocation().getX(), scale) - epsilonW;
        double wXt = Convertor.viewToWorld(drawer.getViewPortLocation().getX() + viewPortWidth, scale) + epsilonW;
        double wYs = Convertor.viewToWorld(drawer.getViewPortLocation().getY(), scale) - epsilonH;
        double wYt = Convertor.viewToWorld(drawer.getViewPortLocation().getY() + viewPortHeight, scale) + epsilonH;

        //print what im asking for
        if (subgroup.contains("EDGES")) {
            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData(group, GroupScale.class);
            double gscale = 1;
            if (groupScale != null) {
                gscale *= groupScale.getCurrentScale(scale, subgroup);
            }

            Collection edges = boundingQuery.get(group, subgroup, wXs, wXt, wYs, wYt);

            draw(canvas, graphData, edges, scale);
        }

        if (subgroup.contains("POLYGONS")) {
            Collection polys = boundingQuery.get(group, subgroup, wXs, wXt, wYs, wYt);
            polys.forEach((polygon) -> draw(canvas, graphData, (GraphPolygon) polygon, scale));
        }

    }

    //cancel scale! with viewportsize no need for it! its already included
    public void draw(Canvas canvas, GraphData graphData, Collection edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        EdgesMetaData edgeMeta = (EdgesMetaData) drawer.getQuery().getMetaData("GRAPH", EdgesMetaData.class);

        if (edges.size() > 0) {
            //temporary hack (Edge)
            Edge edge = (Edge) edges.iterator().next();
            String name = (edge).getId();
            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(name);
            EdgeStroke edgeStroke = edgeMeta.getStroke(edgeData.get(edgeMeta.ROAD_KEY()));
            if (edgeStroke == null) {
                edgeStroke = edgeMeta.getDefaultStroke();
            }

            //get entity zoom and double here
            double pixelLaneWidth = edgeMeta.LANE_WIDTH();
            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData("GRAPH", GroupScale.class);
            if (groupScale != null) {
                pixelLaneWidth *= groupScale.getCurrentScale(scale);
            }
            double tx = drawer.getViewPortLocation().getX();
            double ty = drawer.getViewPortLocation().getY();

            gc.save();
            gc.setLineCap(edgeStroke.getLineCap());
            gc.setLineJoin(edgeStroke.getLineJoin());

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(edgeStroke.getLanes() * pixelLaneWidth * scale);
            strokeEdges(gc, edges, graphData, tx, scale, ty);

            gc.setLineWidth(edgeStroke.getLanes() * (pixelLaneWidth * 0.8) * scale);
            gc.setStroke(edgeStroke.getPaint());
            strokeEdges(gc, edges, graphData, tx, scale, ty);

            gc.restore();

        }
    }

    private void strokeEdges(GraphicsContext gc, Collection edges, GraphData graphData, double tx, double scale, double ty) {
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
            gc.moveTo(Convertor.worldToView(source.getX(), scale) - tx, Convertor.worldToView(source.getY(), scale) - ty);
            gc.lineTo(Convertor.worldToView(target.getX(), scale) - tx, Convertor.worldToView(target.getY(), scale) - ty);
        }
        gc.stroke();
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
        Map.Entry<String, String> entry = polygon.getTags().entrySet().iterator().next();

        StringPair keyVal = new StringPair(entry.getKey(), entry.getValue());
        Paint get = metaData.getKeyTocolors().get(keyVal);
        if (get == null) {
            //System.out.println("unsupported polygon " + keyVal.getFirst() + "=" + keyVal.getSecond());
            get = metaData.getDefaultPaint();
        }
        if (get instanceof Color) {
            gc.setStroke(((Color) get).darker().darker());
        } else {
            gc.setStroke(Color.RED);
        }
        gc.setFill(get);
        gc.setLineWidth(gc.getLineWidth() * 2);
        gc.beginPath();

        gc.moveTo(Convertor.worldToView(source.getX(), scale) - tx, Convertor.worldToView(source.getY(), scale) - ty);
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
            gc.lineTo(Convertor.worldToView(source.getX(), scale) - tx, Convertor.worldToView(source.getY(), scale) - ty);
        }
        gc.closePath();
        gc.stroke();
        gc.fill();
//        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();
    }

}
