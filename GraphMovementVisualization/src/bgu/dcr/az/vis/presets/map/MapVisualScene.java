/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import bgu.dcr.az.vis.presets.map.drawer.FPSDrawer;
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
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
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

    public MapVisualScene(int carNum, String mapPath, GroupBoundingQuery query, SimpleDrawer drawer) {
        super(DEFAULT_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);

        this.boundingQuery = query;
        this.drawer = drawer;

        init(mapPath);

        CanvasLayer front = new CanvasLayer(this);
        MapCanvasLayer back = new MapCanvasLayer(this, graphData, drawer);
        boundingQuery.addMetaData("GRAPH", CanvasLayer.class, back);
        boundingQuery.addMetaData("SPRITES", CanvasLayer.class, back);
        boundingQuery.addMetaData("*FPS*", CanvasLayer.class, front);

        boundingQuery.addMetaData("MOVING", CanvasLayer.class, front);

        GroupScale carZoom = new GroupScale() {
            @Override
            public double getCurrentScale(double worldScale, String subGroup) {
                return super.getCurrentScale(worldScale);
            }
        };
        GroupScale edgeZoom = new GroupScale(1, 1, 0, 10){
            @Override
            public double getCurrentScale(double worldScale, String subGroup) {
                if (subGroup.contains("EDGES")) {
                    return super.getCurrentScale(worldScale);
                } else {
                    return 1;
                }
            }  
        };
        boundingQuery.addMetaData("MOVING", GroupScale.class, carZoom);
        boundingQuery.addMetaData("GRAPH", GroupScale.class, edgeZoom);

        registerLayer(MapCanvasLayer.class, back, back.getCanvas());
        registerLayer(CanvasLayer.class, front, front.getCanvas());

        Point2D.Double bounds = back.getGraphData().getBounds();
        super.setContainerSize(bounds.x, bounds.y);

        sceneProperty().addListener((ObservableValue<? extends Scene> s, Scene o, Scene scene) -> {

            //set default viewport to screen width /height
            scene.windowProperty().addListener((ObservableValue<? extends Window> ov, Window t, Window window) -> {
                window.setOnShown((WindowEvent e) -> {
                    System.out.println("Initializing");
                    drawer.setViewPortWidth(getViewportBounds().getWidth());
                    drawer.setViewPortHeight(getViewportBounds().getHeight());
                    drawer.setScale(1);
                });
            });

            //refresh viewport on window resize
            viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                    //viewport is the amout of meters that we are viewing from the map
                    double viewPortWidth = newBounds.getWidth() / drawer.getScale();
                    double viewPortHeight = newBounds.getHeight() / drawer.getScale();
                    drawer.setViewPortWidth(viewPortWidth);
                    drawer.setViewPortHeight(viewPortHeight);

                    //refresh hvalue and vvalue - will invoke listeners
                    hvalueProperty().set(hvalueProperty().getValue() + 0.0001);
                    vvalueProperty().set(vvalueProperty().getValue() + 0.0001);
                    hvalueProperty().set(hvalueProperty().getValue() - 0.0001);
                    vvalueProperty().set(vvalueProperty().getValue() - 0.0001);
                }
            });

        });

        //handle zoom
        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            if (t.isControlDown()) {
                double scale = drawer.getScale() + t.getDeltaY() / 500.0;
                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                drawer.setScale(scale);

                Point2D.Double mapSize = back.getGraphData().getBounds();
                double mapWidth = mapSize.x;
                double mapHeight = mapSize.y;

                super.setContainerSize(mapWidth * scale, mapHeight * scale);

                double windowWidth = getViewportBounds().getWidth();
                double windownHeight = getViewportBounds().getHeight();

                //viewport is the amout of meters that we are viewing from the map
                double viewPortWidth = windowWidth / scale;
                double viewPortHeight = windownHeight / scale;
                drawer.setViewPortWidth(viewPortWidth);
                drawer.setViewPortHeight(viewPortHeight);

                //refresh hvalue and vvalue - will invoke listeners
                hvalueProperty().set(hvalueProperty().getValue() + 0.0001);
                vvalueProperty().set(vvalueProperty().getValue() + 0.0001);
                hvalueProperty().set(hvalueProperty().getValue() - 0.0001);
                vvalueProperty().set(vvalueProperty().getValue() - 0.0001);

                t.consume();
            }
        });

        hvalueProperty().addListener((ov, n, xRatio) -> {
            double dXRatio = xRatio.doubleValue();
            double scale = drawer.getScale();

            double windowWidth = getViewportBounds().getWidth();

            Point2D.Double mapSize = back.getGraphData().getBounds();
            double mapWidth = mapSize.x;
            double viewportX = dXRatio * (mapWidth - windowWidth / scale);
            double viewportY = drawer.getViewPortLocation().getY();

            drawer.setViewPortLocation(viewportX, viewportY);
        });

        vvalueProperty().addListener((ov, n, yRatio) -> {
            double dYRatio = yRatio.doubleValue();
            double scale = drawer.getScale();

            double windowHeight = getViewportBounds().getHeight();

            Point2D.Double mapSize = back.getGraphData().getBounds();
            double mapHeight = mapSize.y;
            double viewportY = dYRatio * (mapHeight - windowHeight / scale);
            double viewportX = drawer.getViewPortLocation().getX();

            drawer.setViewPortLocation(viewportX, viewportY);

        });

        Image greenCarImage = new Image(R.class.getResourceAsStream("car-green.jpg"));
        Image blueCarImage = new Image(R.class.getResourceAsStream("car-blue.jpg"));
        for (long i = 0; i < carNum; i++) {
            DefinedSizeSpriteBasedEntity car = new DefinedSizeSpriteBasedEntity(i, (Math.random() > 0.5) ? greenCarImage : blueCarImage, DefinedSizeSpriteBasedEntity.SizeParameter.WIDTH, 1.7);
            boundingQuery.addToGroup("MOVING", "CARS", 10, 10, car.getRealHeight(), car.getRealWidth(), car);
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

    private void init(String mapFilePath) {
        graphData = new GraphReader().readGraph(mapFilePath);

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

                //this is a temporary hack for hasID
                Edge temp = new Edge(edge);
                boundingQuery.addToGroup("GRAPH", "EDGES." + edgeType, source.getX(), source.getY(), width, height, temp);

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

            //is building sprite drawing enabled
            boolean buildingSpriteOn = false;
            if (key.equals("building") && buildingSpriteOn) {
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
                DefinedSizeSpriteBasedEntity entity = new DefinedSizeSpriteBasedEntity(i, buildingImage, newW, newH);
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
        boundingQuery.addMetaData("MOVING", GroupDrawer.class, spriteDrawer);
    }

}
