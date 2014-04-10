/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.SimpleScrollableVisualScene;
import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import bgu.dcr.az.vis.player.impl.entities.SpriteBasedEntity;
import bgu.dcr.az.vis.presets.map.drawer.EdgesMetaData;
import bgu.dcr.az.vis.presets.map.drawer.GraphDrawer;
import bgu.dcr.az.vis.presets.map.drawer.GroupDrawer;
import bgu.dcr.az.vis.presets.map.drawer.PolygonMetaData;
import bgu.dcr.az.vis.presets.map.drawer.SimpleDrawer;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class MapVisualScene extends SimpleScrollableVisualScene {

    public static final double MIN_SCALE = 0.01;
    public static final double MAX_SCALE = 10;

    private static double DEFAULT_CONTAINER_WIDTH = 10000;
    private static double DEFAULT_CONTAINER_HEIGHT = 10000;

    private GraphData graphData;
    private GroupBoundingQuery boundingQuery;
    private SimpleDrawer drawer;

    public MapVisualScene(int carNum, String mapPath) {
        super(DEFAULT_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);

        init(mapPath);
        CanvasLayer front = new CanvasLayer(this);
        MapCanvasLayer back = new MapCanvasLayer(this, graphData, drawer);
        boundingQuery.addMetaData("GRAPH", CanvasLayer.class, back);

        registerLayer(MapCanvasLayer.class, back, back.getCanvas());
        registerLayer(CanvasLayer.class, front, front.getCanvas());

        Point2D.Double bounds = back.getGraphData().getBounds();
        super.setContainerSize(bounds.x, bounds.y);

        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double scale = back.getScale() + t.getDeltaY() / 500;
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
//                scaleProperty().set(scale);
                drawer.setScale(scale);

//                double mousePointX = t.getSceneX() + back.getCanvas().getTranslateX();
//                double mousePointY = t.getSceneY() + back.getCanvas().getTranslateY();
//
//                double newHval = (mousePointX) / (CONTAINER_WIDTH - getViewportBounds().getWidth());
//                double newVval = (mousePointY) / (CONTAINER_HEIGHT - getViewportBounds().getHeight());
//                setHvalue(newHval);
//                setVvalue(newVval);
                back.drawGraph();
                t.consume();
            }
        });

//        widthProperty().addListener((ov, o, n) -> back.drawGraph());
//        heightProperty().addListener((ov, o, n) -> back.drawGraph());
        
        //notice that SimpleScrollableVisualScene.java - register layer - also does this!!!
        hvalueProperty().addListener((ov, n, o) -> {
//            back.drawGraph();
            

        });
        vvalueProperty().addListener((ov, n, o) -> {
//            back.drawGraph();
            
        
        });

        Image greenCarImage = new Image(R.class.getResourceAsStream("car-green.jpg"));
        Image blueCarImage = new Image(R.class.getResourceAsStream("car-blue.jpg"));
        for (long i = 0; i < carNum; i++) {
            addEntity(i, new DefinedSizeSpriteBasedEntity(i, CanvasLayer.class, (Math.random() > 0.5) ? greenCarImage : blueCarImage, DefinedSizeSpriteBasedEntity.SizeParameter.WIDTH, 1.7));
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

    private void init(String mapFilePath) {
        graphData = new GraphReader().readGraph(mapFilePath);

        boundingQuery = new GroupBoundingQuery();
        drawer = new SimpleDrawer(boundingQuery);

        for (String edgeType : graphData.getTagToEdge().keySet()) {
            Collection<String> edges = graphData.getTagToEdge().get(edgeType);
            boundingQuery.createGroup("GRAPH", "EDGES." + edgeType, false);
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
                boundingQuery.addToGroup("GRAPH", "EDGES." + edgeType, source.getX(), source.getY(), width, height, edge);
            }
        }
        LinkedList<GraphPolygon> polys = graphData.getPolygons();
        boundingQuery.createGroup("building", "BACKGROUND", false);
//        boundingQuery.addMetaData("building", SimplePolygonImageDrawer.class, backGroundImageDrawer);
        boundingQuery.createGroup("GRAPH", "POLYGONS.leisure", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.landuse", false);
        boundingQuery.createGroup("GRAPH", "POLYGON.defaultPolys", false);

        for (GraphPolygon poly : polys) {
            String key = poly.getParams().entrySet().iterator().next().getKey();
            if (boundingQuery.hasSubGroup("GRAPH", "POLYGONS." + key)) {
                boundingQuery.addToGroup("GRAPH", "POLYGONS." + key, poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            } else {
                boundingQuery.addToGroup("GRAPH", "POLYGON.defaultPolys", poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            }
        }

        boundingQuery.addMetaData("GRAPH", GroupDrawer.class, new GraphDrawer(drawer, graphData));
        boundingQuery.addMetaData("GRAPH", EdgesMetaData.class, new EdgesMetaData());
        boundingQuery.addMetaData("GRAPH", PolygonMetaData.class, new PolygonMetaData());
    }

}
