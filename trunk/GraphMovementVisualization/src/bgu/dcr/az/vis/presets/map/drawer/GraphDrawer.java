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

//        System.out.println("drawing graph");
        
        GroupBoundingQuery boundingQuery = drawer.getQuery();
        double scale = drawer.getScale();

        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData("GRAPH", CanvasLayer.class);
        Canvas canvas = canvasLayer.getCanvas();

        //print what im asking for
        if (subgroup.contains("EDGES")) {
            
//            boolean isCaching = canvas.isCache();
//            canvas.setCache(false);
            
            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData(group, GroupScale.class);
            double gscale = 1;
            if (groupScale != null) {
                gscale *= groupScale.getCurrentScale(scale, subgroup);
            }

            Collection edges = boundingQuery.getCurrentFrameEntities(group, subgroup, drawer);

            draw(canvas, graphData, edges, scale);
//            canvas.setCache(isCaching);
        }

        if (subgroup.contains("POLYGONS")) {
            Collection polys = boundingQuery.getCurrentFrameEntities(group, subgroup, drawer);
            polys.forEach((polygon) -> draw(canvas, graphData, (GraphPolygon) polygon));
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
            strokeEdges(gc, edges, graphData);

            gc.setLineWidth(edgeStroke.getLanes() * (pixelLaneWidth * 0.8) * scale);
            gc.setStroke(edgeStroke.getPaint());
            strokeEdges(gc, edges, graphData);

            gc.restore();

        }
    }
    
    private void strokeEdges(GraphicsContext gc, Collection edges, GraphData graphData) {
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
            Location frameFrom = drawer.worldToFrame(source.getX(), source.getY());
            Location frameTo = drawer.worldToFrame(target.getX(), target.getY());
            gc.moveTo(frameFrom.getX(), frameFrom.getY());
            gc.lineTo(frameTo.getX(), frameTo.getY());
        }
        
        gc.stroke();
    }
    
    private double [] xs;
    private double [] ys;
    
    private void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon) {
        
        PolygonMetaData metaData = (PolygonMetaData) drawer.getQuery().getMetaData("GRAPH", PolygonMetaData.class);

        GraphicsContext gc = canvas.getGraphicsContext2D();
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
        
//        int size = polygon.getNodes().size();
//        
//        if (xs == null || xs.length < size) {
//            xs = new double[size];
//            ys = new double[size];
//        }
        
        gc.setFill(get);
        gc.setLineWidth(gc.getLineWidth() * 2);
        
        gc.beginPath();
        Location frameLoc = drawer.worldToFrame(source.getX(), source.getY());
        gc.moveTo(frameLoc.getX(), frameLoc.getY());
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
            frameLoc = drawer.worldToFrame(source.getX(), source.getY());
            gc.lineTo(frameLoc.getX(), frameLoc.getY());
        }
        gc.closePath();
        gc.stroke();
        gc.fill();
        
//        int i = 0;        
//        for (String n : polygon.getNodes()) {
//            source = (AZVisVertex) graphData.getData(node);
//            Location frameLoc = drawer.worldToFrame(source.getX(), source.getY());
//            xs[i] = frameLoc.getX();
//            ys[i] = frameLoc.getY();
//            i++;
//        }
//        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();
    }

}
