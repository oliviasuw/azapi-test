/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.player.api.VisualScene;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import com.bbn.openmap.util.quadtree.QuadTree;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import data.map.impl.wersdfawer.groupbounding.GroupMetaData;
import graphics.graph.EdgeDrawer;
import graphics.graph.GraphDrawer;
import graphics.graph.PolygonDrawer;
import graphics.graph.SimplePolygonImageDrawer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import javafx.scene.CacheHint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class MapCanvasLayer extends CanvasLayer {

    private final GraphData graphData;
    private final GraphDrawer graphDrawer;
    private final GroupBoundingQuery boundingQuery;
    private EdgeDrawer edgeDrawer;
    private PolygonDrawer polyDrawer;
    private SimplePolygonImageDrawer backGroundImageDrawer;

    /**
     * generates a new canvas layer map from the specified file path. the map is
     * represented by a jgrapht graph.
     *
     * @param scene
     * @param mapFilePath
     */
    public MapCanvasLayer(VisualScene scene, String mapFilePath) {
        super(scene);
        graphData = new GraphReader().readGraph(mapFilePath);

        boundingQuery = new GroupBoundingQuery();
        for (String edgeType : graphData.getTagToEdge().keySet()) {
            Collection<String> edges = graphData.getTagToEdge().get(edgeType);
            boundingQuery.createGroup(edgeType, "EDGES", new GroupMetaData(), false);
            for (String edge : edges) {
                AZVisVertex source;
                AZVisVertex target;
                try {
                    source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edge));
                    target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edge));
                } catch (Exception e) {
                    System.out.println("problem with drawing! cant find some node.");
                    continue;
                }
                double width = Math.abs(target.getX() - source.getX());
                double height = Math.abs(target.getY() - source.getY());
                boundingQuery.addToGroup(edgeType, source.getX(),source.getY(), width, height, edge);
            }
        }
        LinkedList<GraphPolygon> polys = graphData.getPolygons();
        boundingQuery.createGroup("building", "BACKGROUND", new GroupMetaData(), false);
        boundingQuery.createGroup("leisure", "BACKGROUND", new GroupMetaData(), false);
        boundingQuery.createGroup("landuse", "BACKGROUND", new GroupMetaData(), false);
        boundingQuery.createGroup("defaultPolys", "BACKGROUND", new GroupMetaData(), false);

        Set<String> groups = boundingQuery.getGroups();
        for (GraphPolygon poly : polys) {
            String key = poly.getParams().entrySet().iterator().next().getKey();
            if (boundingQuery.hasGroup(key)) {
                boundingQuery.addToGroup(key, poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            } else {
                boundingQuery.addToGroup("defaultPolys", poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            }
        }

        graphDrawer = new GraphDrawer(boundingQuery);
//        graphDrawer.drawGraph(getCanvas(), graphData, 1);
        getCanvas().setCacheHint(CacheHint.SPEED);
        getCanvas().setCache(true);
    }

    public GraphData getGraphData() {
        return graphData;
    }

    @Override
    public void refresh() {

    }

    public void drawGraph() {
//        graphDrawer.drawGraph(getCanvas(), graphData, getScale());

         edgeDrawer = new EdgeDrawer();
         polyDrawer = new PolygonDrawer();
         backGroundImageDrawer = new SimplePolygonImageDrawer();

        GraphicsContext gc = getCanvas().getGraphicsContext2D();

        double tx = getCanvas().getTranslateX();
        double ty = getCanvas().getTranslateY();

        gc.setFill(new Color(0, 0, 0, 1));
        gc.clearRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

        gc.strokeText("tx: " + tx + ", ty: " + ty, 14, getCanvas().getHeight() - 14);
        gc.strokeText("scale: " + getScale() + " meter/pixel", 14, getCanvas().getHeight() - 25);
        double scale = getScale();
   
        for (String edgeType : graphData.getTagToEdge().keySet()) {
            double epsilonH = boundingQuery.getEpsilon(edgeType)[0];
            double epsilonW = boundingQuery.getEpsilon(edgeType)[1];
            Collection edges = boundingQuery.get(edgeType, (tx - epsilonW) * scale, (tx + getCanvas().getWidth() + epsilonW) * scale, (ty + getCanvas().getHeight() + epsilonH) * scale, (ty - epsilonH) * scale);
            edgeDrawer.draw(getCanvas(), graphData, edges, getScale());
        }

        for (String group : boundingQuery.getGroups()) {
            if (boundingQuery.getSubGroup(group).equals("EDGES")) {
                continue;
            }
            double epsilonH = boundingQuery.getEpsilon(group)[0];
            double epsilonW = boundingQuery.getEpsilon(group)[1];
            Vector polys = (Vector) boundingQuery.get(group, tx - epsilonW ,tx + getCanvas().getWidth() + epsilonW,getCanvas().getTranslateY() + getCanvas().getHeight() + epsilonH,  getCanvas().getTranslateY() - epsilonH );
            polys.sort(new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    GraphPolygon poly1 = (GraphPolygon) o1;
                    GraphPolygon poly2 = (GraphPolygon) o2;
                    return (poly1.getCenter().y > poly2.getCenter().y) ? 1 : -1;
                }
            });
            for (Iterator it = polys.iterator(); it.hasNext();) {
                Object obj = it.next();
                GraphPolygon polygon = (GraphPolygon) obj;
                polyDrawer.draw(getCanvas(), graphData, polygon, scale);
                polyDrawer.draw(getCanvas(), graphData, polygon, scale);
                backGroundImageDrawer.draw(getCanvas(), graphData, polygon, scale);
            }
        }
    }

}
