/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.presets.map.GroupScale;
import bgu.dcr.az.vis.tools.Location;
import bgu.dcr.az.vis.tools.StringPair;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
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

        //print what im asking for
        if (subgroup.contains("EDGES")) {
            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData(group, GroupScale.class);
            double gscale = 1;
            if (groupScale != null) {
                gscale *= groupScale.getCurrentScale(scale, subgroup);
            }
            Collection edges = boundingQuery.get(group, subgroup, drawer.getViewPortLocation().getX() - epsilonW * scale * gscale, drawer.getViewPortLocation().getX() + viewPortWidth + epsilonW * scale *gscale, drawer.getViewPortLocation().getY() + viewPortHeight + epsilonH * scale *gscale, drawer.getViewPortLocation().getY() - epsilonH * scale *gscale);
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

        EdgesMetaData edgeMeta = (EdgesMetaData) drawer.getQuery().getMetaData("GRAPH", EdgesMetaData.class);

        if (edges.size() > 0) {
            //temporary hack (Edge)
            String name = ((Edge) edges.iterator().next()).getId();
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
            gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
            gc.lineTo((target.getX() - tx) * scale, (target.getY() - ty) * scale);
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
        Map.Entry<String, String> entry = polygon.getParams().entrySet().iterator().next();

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

        gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
        int i = 1;
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
            gc.lineTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
        }
        gc.closePath();
        gc.stroke();
        gc.fill();
//        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();
    }

}
