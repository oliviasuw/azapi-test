/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import bgu.dcr.az.vis.player.api.VisualScene;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.presets.map.drawer.DrawerInterface;
import bgu.dcr.az.vis.presets.map.drawer.EdgesMetaData;
import bgu.dcr.az.vis.presets.map.drawer.GraphDrawer;
import bgu.dcr.az.vis.presets.map.drawer.GroupDrawer;
import bgu.dcr.az.vis.presets.map.drawer.PolygonMetaData;
import bgu.dcr.az.vis.presets.map.drawer.SimpleDrawer;
import com.bbn.openmap.util.quadtree.QuadTree;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import graphics.graph.EdgeDrawer;
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

    private GraphData graphData;
    private DrawerInterface drawer;

//    private EdgeDrawer edgeDrawer;
//    private PolygonDrawer polyDrawer;
//    private SimplePolygonImageDrawer backGroundImageDrawer;
    /**
     * generates a new canvas layer map from the specified file path. the map is
     * represented by a jgrapht graph.
     *
     * @param scene
     * @param mapFilePath
     */
    public MapCanvasLayer(VisualScene scene, GraphData graphData, DrawerInterface drawer) {
        super(scene);
        this.graphData = graphData;
        this.drawer = drawer;

//        graphDrawer = new GraphDrawer(boundingQuery);
//        graphDrawer.drawGraph(getCanvas(), graphData, 1);
        getCanvas().setCacheHint(CacheHint.SPEED);
        getCanvas().setCache(true);
    }

    public GraphData getGraphData() {
        return graphData;
    }

    @Override
    public void refresh() {
        drawer.draw();
    }

    public void drawGraph() {
//        graphDrawer.drawGraph(getCanvas(), graphData, getScale());

        GraphicsContext gc = getCanvas().getGraphicsContext2D();

        double tx = getCanvas().getTranslateX();
        double ty = getCanvas().getTranslateY();

        gc.setFill(new Color(0, 0, 0, 1));
        gc.clearRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

        gc.strokeText("tx: " + tx + ", ty: " + ty, 14, getCanvas().getHeight() - 14);
        gc.strokeText("scale: " + getScale() + " meter/pixel", 14, getCanvas().getHeight() - 25);
        double scale = getScale();

//        for (String edgeType : graphData.getTagToEdge().keySet()) {
//            double epsilonH = boundingQuery.getEpsilon("EDGES", edgeType)[0];
//            double epsilonW = boundingQuery.getEpsilon("EDGES", edgeType)[1];
//            Collection edges = boundingQuery.get("EDGES", edgeType, (tx - epsilonW) * scale, (tx + getCanvas().getWidth() + epsilonW) * scale, (ty + getCanvas().getHeight() + epsilonH) * scale, (ty - epsilonH) * scale);
//            edgeDrawer.draw(getCanvas(), graphData, edges, getScale());
//        }
//
//        for (String group : boundingQuery.getGroups()) {
//            if (group.equals("EDGES")) {
//                continue;
//            }
//            for (String subgroup : boundingQuery.getSubGroups(group)) {
//                double epsilonH = boundingQuery.getEpsilon(group, subgroup)[0];
//                double epsilonW = boundingQuery.getEpsilon(group, subgroup)[1];
//                Vector polys = (Vector) boundingQuery.get(group, subgroup, tx - epsilonW, tx + getCanvas().getWidth() + epsilonW, getCanvas().getTranslateY() + getCanvas().getHeight() + epsilonH, getCanvas().getTranslateY() - epsilonH);
//                polys.sort(new Comparator() {
//
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        GraphPolygon poly1 = (GraphPolygon) o1;
//                        GraphPolygon poly2 = (GraphPolygon) o2;
//                        return (poly1.getCenter().y > poly2.getCenter().y) ? 1 : -1;
//                    }
//                });
//                for (Iterator it = polys.iterator(); it.hasNext();) {
//                    Object obj = it.next();
//                    GraphPolygon polygon = (GraphPolygon) obj;
//                    polyDrawer.draw(getCanvas(), graphData, polygon, scale);
//                    backGroundImageDrawer.draw(getCanvas(), graphData, polygon, scale);
//                }
//            }
//        }
    }

}
