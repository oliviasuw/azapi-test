/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.presets.map.GroupScale;
import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeStroke;
import java.util.Collection;
import java.util.HashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shlomi
 */
public class DynamicColorDrawer extends GroupDrawer {

    private final GraphData graphData;
    int baseR = 240;
    int baseG = 66;
    int baseB = 74;
    double multiplier = 0.2;

    public DynamicColorDrawer(GraphData graphData, DrawerInterface drawer) {
        super(drawer);
        this.graphData = graphData;
    }

    @Override
    public void _draw(String group, String subgroup) {
        GroupBoundingQuery boundingQuery = drawer.getQuery();
        double scale = drawer.getScale();

        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData(group, CanvasLayer.class);
        Canvas canvas = canvasLayer.getCanvas();

        if (subgroup.contains("EDGES")) {

            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData(group, GroupScale.class);
            double gscale = 1;
            if (groupScale != null) {
                gscale *= groupScale.getCurrentScale(scale, subgroup);
            }

            Collection edges = boundingQuery.getCurrentFrameEntities(group, subgroup, drawer);

            draw(canvas, graphData, edges, scale);
        }

    }

    //cancel scale! with viewportsize no need for it! its already included
    public void draw(Canvas canvas, GraphData graphData, Collection edges, double scale) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        EdgesMetaData edgeMeta = (EdgesMetaData) drawer.getQuery().getMetaData("GRAPH", EdgesMetaData.class);

        gc.save();
        for (Object currEdge : edges) {
            Edge edge = (Edge) currEdge;
            String[] split = (edge).getId().split(" ");
            String name = split[0] + " " + split[1];            
            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(name);
            EdgeStroke edgeStroke = edgeMeta.getStroke(edgeData.get(edgeMeta.ROAD_KEY()));
            if (edgeStroke == null) {
                edgeStroke = edgeMeta.getDefaultStroke();
            }
            double pixelLaneWidth = edgeMeta.LANE_WIDTH();
            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData("GRAPH", GroupScale.class);
            if (groupScale != null) {
                pixelLaneWidth *= groupScale.getCurrentScale(scale);
            }
            double tx = drawer.getViewPortLocation().getX();
            double ty = drawer.getViewPortLocation().getY();
            gc.setLineWidth(edgeStroke.getLanes() * (pixelLaneWidth * 0.8) * scale);
            int entityNum = graphData.getEdgeEntities(name).size();
//            double mult = Math.pow(multiplier,entityNum);
//            gc.setStroke(Color.color((int)(baseR*mult*baseR), (int)(baseG*mult*baseG), (int)(baseB*mult*baseB)));
            gc.setStroke(Color.color(baseR,baseG,baseB));
            AZVisVertex source = null;
            AZVisVertex target = null;
            try {
                source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(name));
                target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(name));
            } catch (Exception e) {
                System.out.println("problem with drawing! cant find some node.");
                continue;
            }
            Location frameFrom = drawer.worldToFrame(source.getX(), source.getY());
            Location frameTo = drawer.worldToFrame(target.getX(), target.getY());
            gc.strokeLine(frameFrom.getX(), frameFrom.getY(), frameTo.getX(), frameTo.getY());
        
        }
        gc.restore();
        
//        if (edges.size() > 0) {
//            //temporary hack (Edge)
//            Edge edge = (Edge) edges.iterator().next();
//            String[] split = (edge).getId().split(" ");
//            String name = split[0] + " " + split[1];
//            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(name);
//            EdgeStroke edgeStroke = edgeMeta.getStroke(edgeData.get(edgeMeta.ROAD_KEY()));
//            if (edgeStroke == null) {
//                edgeStroke = edgeMeta.getDefaultStroke();
//            }
//
//            //get entity zoom and double here
//            double pixelLaneWidth = edgeMeta.LANE_WIDTH();
//            GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData("GRAPH", GroupScale.class);
//            if (groupScale != null) {
//                pixelLaneWidth *= groupScale.getCurrentScale(scale);
//            }
//            double tx = drawer.getViewPortLocation().getX();
//            double ty = drawer.getViewPortLocation().getY();
//
//            gc.save();
//            gc.setLineCap(edgeStroke.getLineCap());
//            gc.setLineJoin(edgeStroke.getLineJoin());
//
////            gc.setStroke(Color.RED);
////            gc.setLineWidth(edgeStroke.getLanes() * pixelLaneWidth * scale);
////            strokeEdges(gc, edges, graphData);
//            gc.setLineWidth(edgeStroke.getLanes() * (pixelLaneWidth * 0.8) * scale);
//
//            gc.setStroke(Color.RED);
//            strokeEdges(gc, edges, graphData);
//
//            gc.restore();
//
//        }
    }

//    private void strokeEdges(GraphicsContext gc, Collection edges, GraphData graphData) {
//        gc.beginPath();
//        for (Object edgeObj : edges) {
////                String edgeName = (String) edgeObj;
//            //temporary hack:
//            String edgeName = ((Edge) edgeObj).getId();
//            String[] split = edgeName.split(" ");
//            edgeName = split[0] + " " + split[1];
//
//            AZVisVertex source = null;
//            AZVisVertex target = null;
//            try {
//                source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
//                target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
//            } catch (Exception e) {
//                System.out.println("problem with drawing! cant find some node.");
//                continue;
//            }
//            Location frameFrom = drawer.worldToFrame(source.getX(), source.getY());
//            Location frameTo = drawer.worldToFrame(target.getX(), target.getY());
//            gc.moveTo(frameFrom.getX(), frameFrom.getY());
//            gc.lineTo(frameTo.getX(), frameTo.getY());
//        }
//        
//        gc.stroke();
//    }

}
