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
import bgu.dcr.az.vis.presets.map.drawer.SpriteDrawer;
import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class MapVisualScene extends SimpleScrollableVisualScene {

    public static final double MIN_SCALE = 0.3;
    public static final double MAX_SCALE = 10;

    private static double DEFAULT_CONTAINER_WIDTH = 10000;
    private static double DEFAULT_CONTAINER_HEIGHT = 10000;

    private GraphData graphData;
    private GroupBoundingQuery boundingQuery;
    private SimpleDrawer drawer;
    private HashMap<String, Image> images;

    public MapVisualScene(int carNum, String mapPath) {
        super(DEFAULT_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);

        init(mapPath);
        CanvasLayer front = new CanvasLayer(this);
        MapCanvasLayer back = new MapCanvasLayer(this, graphData, drawer);
        boundingQuery.addMetaData("GRAPH", CanvasLayer.class, back);
        boundingQuery.addMetaData("SPRITES", CanvasLayer.class, back);
        
        boundingQuery.addMetaData("MOVING", CanvasLayer.class, front);

        registerLayer(MapCanvasLayer.class, back, back.getCanvas());
        registerLayer(CanvasLayer.class, front, front.getCanvas());

        Point2D.Double bounds = back.getGraphData().getBounds();
        super.setContainerSize(bounds.x, bounds.y);

        //set default viewport to screen width /height
        drawer.setViewPortWidth(getViewportBounds().getWidth());
        drawer.setViewPortHeight(getViewportBounds().getHeight());

        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double scale = drawer.getScale() + t.getDeltaY() / 500;
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                drawer.setScale(scale);

                //problematic lines
                Point2D.Double b = back.getGraphData().getBounds();
                pane.setPrefSize(b.x * scale, b.y * scale);

                double screenWidth = getViewportBounds().getWidth();
                double screenHeight = getViewportBounds().getHeight();

                //multiplying screen width/height by scale will yield it in meters
                double viewPortWidth = screenWidth / scale;
                double viewPortHeight = screenHeight / scale;
                drawer.setViewPortWidth(viewPortWidth);
                drawer.setViewPortHeight(viewPortHeight);

//                back.drawGraph();
                t.consume();
            }
        });

//        widthProperty().addListener((ov, o, n) -> back.drawGraph());
//        heightProperty().addListener((ov, o, n) -> back.drawGraph());
        //notice that SimpleScrollableVisualScene.java - register layer - also does this!!!
        hvalueProperty().addListener((ov, n, o) -> {
//            back.drawGraph();
            double scale = drawer.getScale();
            double screenWidth = getViewportBounds().getWidth();
            double screenHeight = getViewportBounds().getHeight();

            //multiplying screen width/height by scale will yield it in meters
            double viewPortWidth = screenWidth / scale;
            double viewPortHeight = screenHeight / scale;
            drawer.setViewPortWidth(viewPortWidth);
            drawer.setViewPortHeight(viewPortHeight);

            double x = o.doubleValue() * (pane.getWidth() - screenWidth);
            double y = drawer.getViewPortLocation().getY();

            drawer.setViewPortLocation(x, y);

//            hvalueProperty().addListener((ov, n, o) -> layerNode.translateXProperty().set(o.doubleValue() * (pane.getWidth() - getViewportBounds().getWidth())));
//            vvalueProperty().addListener((ov, n, o) -> layerNode.translateYProperty().set(o.doubleValue() * (pane.getHeight() - getViewportBounds().getHeight())));
        });
        vvalueProperty().addListener((ov, n, o) -> {
//            back.drawGraph();

            double scale = drawer.getScale();
            double screenWidth = getViewportBounds().getWidth();
            double screenHeight = getViewportBounds().getHeight();

            double x = drawer.getViewPortLocation().getX();
            double y = o.doubleValue() * (pane.getHeight() - screenHeight);

            drawer.setViewPortLocation(x, y);

        });

        Image greenCarImage = new Image(R.class.getResourceAsStream("car-green.jpg"));
        Image blueCarImage = new Image(R.class.getResourceAsStream("car-blue.jpg"));
        for (long i = 0; i < carNum; i++) {
            DefinedSizeSpriteBasedEntity car = new DefinedSizeSpriteBasedEntity(i, CanvasLayer.class, (Math.random() > 0.5) ? greenCarImage : blueCarImage, DefinedSizeSpriteBasedEntity.SizeParameter.WIDTH, 1.7);
            addEntity(i, car);

            boundingQuery.addToGroup("MOVING", "CARS", 10, 10, car.getRealHeight(), car.getRealWidth(), car);
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

    private void init(String mapFilePath) {
        graphData = new GraphReader().readGraph(mapFilePath);

        boundingQuery = new GroupBoundingQuery();
        drawer = new SimpleDrawer(boundingQuery);

        images = new HashMap<>();
        images.put("university", new Image(R.class.getResourceAsStream("university.png")));
        images.put("school", new Image(R.class.getResourceAsStream("university.png")));
        images.put("office", new Image(R.class.getResourceAsStream("office.png")));
        Image defaultImage = new Image(R.class.getResourceAsStream("building.png"));

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
        boundingQuery.createGroup("SPRITES", "building", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.leisure", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.landuse", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.building", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.defaultPolys", false);

        int i = 0;
        for (GraphPolygon poly : polys) {
            String key = poly.getParams().entrySet().iterator().next().getKey();
            if (boundingQuery.hasSubGroup("GRAPH", "POLYGONS." + key)) {
                boundingQuery.addToGroup("GRAPH", "POLYGONS." + key, poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            } else {
                boundingQuery.addToGroup("GRAPH", "POLYGONS.defaultPolys", poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            }

            if (key.equals("building")) {
                double subScale = Math.sqrt(Math.abs(poly.getArea()));
                Image buildingImage = images.get(poly.getParams().get("building"));
                if (buildingImage == null) {
                    buildingImage = defaultImage;
                }
                double newW = subScale;
                double newH = (buildingImage.getHeight() / buildingImage.getWidth()) * newW;
//                if (newH > MAX_BUILDING_HEIGHT) {
//                    newW = newW / newH * MAX_BUILDING_HEIGHT;
//                    newH = MAX_BUILDING_HEIGHT;
//                }
                DefinedSizeSpriteBasedEntity entity = new DefinedSizeSpriteBasedEntity(i, MapCanvasLayer.class, buildingImage, newW, newH);
                entity.setLocation(new Location(poly.getCenter().x, poly.getCenter().y));
                boundingQuery.addToGroup("SPRITES", "building", poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), entity);
                i++;
            }
        }

        boundingQuery.addMetaData("GRAPH", GroupDrawer.class, new GraphDrawer(drawer, graphData));
        boundingQuery.addMetaData("GRAPH", EdgesMetaData.class, new EdgesMetaData());
        boundingQuery.addMetaData("GRAPH", PolygonMetaData.class, new PolygonMetaData());

        SpriteDrawer spriteDrawer = new SpriteDrawer(drawer);
        boundingQuery.addMetaData("SPRITES", GroupDrawer.class, spriteDrawer);

        boundingQuery.createGroup("MOVING", "CARS", true);
        boundingQuery.addMetaData("MOVING",GroupDrawer.class, spriteDrawer);
    }

}
