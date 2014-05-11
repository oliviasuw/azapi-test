/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Shl
 */
public class MovingCarDrawer extends GroupDrawer {

    private static final int MAX_SPRITE_HEIGHT = 100;
    private GraphData graphData;

    public MovingCarDrawer(DrawerInterface drawer, GraphData graphData) {
        super(drawer);
        this.graphData = graphData;
    }

    @Override
    public void _draw(String group, String subgroup) {
//        GroupBoundingQuery boundingQuery = drawer.getQuery();
//        Location viewPortLocation = drawer.getViewPortLocation();
//
//        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData(group, CanvasLayer.class);
//        Canvas canvas = canvasLayer.getCanvas();
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        double scale = drawer.getScale();
//        double viewPortWidth = drawer.getViewPortWidth();
//        double viewPortHeight = drawer.getViewPortHeight();
//
//        for (String graphSubGroup : boundingQuery.getSubGroups("GRAPH")) {
//            if (subgroup.contains("EDGES")) {
//                double epsilonH = boundingQuery.getEpsilon("EDGES", subgroup)[1];
//                double epsilonW = boundingQuery.getEpsilon("EDGES", subgroup)[0];
//                Collection edges = boundingQuery.get("GRAPH", graphSubGroup, drawer.getViewPortLocation().getX() - epsilonW * scale, drawer.getViewPortLocation().getX() + viewPortWidth + epsilonW * scale, drawer.getViewPortLocation().getY() + viewPortHeight + epsilonH * scale, drawer.getViewPortLocation().getY() - epsilonH * scale);
//                for (Object edge : edges) {
//                    String edgeName = (String)edge;
////                    graphData.getEdgeEntities(edgeName)
//                }
//            }
//        }
//
//        Vector entities = (Vector) boundingQuery.get(group, subgroup, viewPortLocation.getX() - epsilonW * scale, viewPortLocation.getX() + viewPortWidth + epsilonW * scale, viewPortLocation.getY() + viewPortHeight + epsilonH * scale, viewPortLocation.getY() - epsilonH * scale);
//        if (!boundingQuery.isMoveable(group, subgroup)) {
//            entities.sort(new Comparator() {
//
//                @Override
//                public int compare(Object o1, Object o2) {
//                    DefinedSizeSpriteBasedEntity entity1 = (DefinedSizeSpriteBasedEntity) o1;
//                    DefinedSizeSpriteBasedEntity entity2 = (DefinedSizeSpriteBasedEntity) o2;
//                    return (entity1.getLocation().getY() > entity2.getLocation().getY()) ? 1 : -1;
//                }
//            });
//        }
//        for (Iterator it = entities.iterator(); it.hasNext();) {
//            Object obj = it.next();
//            DefinedSizeSpriteBasedEntity entity = (DefinedSizeSpriteBasedEntity) obj;
//            double centerX = entity.getLocation().getX();
//            double centerY = entity.getLocation().getY();
//            double tx = drawer.getViewPortLocation().getX();
//            double ty = drawer.getViewPortLocation().getY();
//            double newH = entity.getRealHeight() * scale;
//            double newW = entity.getRealWidth() * scale;
//            if (newH > MAX_SPRITE_HEIGHT) {
//                newW = newW / newH * MAX_SPRITE_HEIGHT;
//                newH = MAX_SPRITE_HEIGHT;
//            }
//            gc.drawImage(entity.getImage(), (centerX - tx - ((newW / scale) / 2.0)) * scale, (centerY - ty - ((newH / scale) / 2.0)) * scale, newW, newH);
//        }
    }

}
