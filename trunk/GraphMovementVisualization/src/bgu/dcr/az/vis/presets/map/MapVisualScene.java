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
import bgu.dcr.az.vis.tools.Convertor;
import bgu.dcr.az.vis.tools.Location;
import bgu.dcr.az.vis.tools.StringPair;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import data.map.impl.wersdfawer.GraphReader;
import data.map.impl.wersdfawer.NewGraphReader;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    public static final double TOOLTIP_RECT_WIDTH = 10;

    private static double DEFAULT_CONTAINER_WIDTH = 10000;
    private static double DEFAULT_CONTAINER_HEIGHT = 10000;

    private GraphData graphData;
    private GroupBoundingQuery boundingQuery;
    private SimpleDrawer drawer;
    private HashMap<StringPair, Image> images;

    private Tooltip tooltip = new Tooltip();
    private Rectangle tooltipRect;

    public MapVisualScene(int carNum, String mapPath, GroupBoundingQuery query, SimpleDrawer drawer) {
        super(DEFAULT_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);

        //set a tooltip to show entity data
        this.boundingQuery = query;
        this.drawer = drawer;
        tooltipRect = new Rectangle(TOOLTIP_RECT_WIDTH, TOOLTIP_RECT_WIDTH, Color.RED);
        pane.getChildren().add(tooltipRect);
//        tooltipRect.toFront();
        tooltip.maxWidth(50);
        tooltip.maxHeight(50);
        Tooltip.install(tooltipRect, tooltip);

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
        GroupScale sameSizeZoom = new GroupScale(1, 1, 0, 10) {
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
        boundingQuery.addMetaData("GRAPH", GroupScale.class, sameSizeZoom);

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
                    handleResize(newBounds);
                }
            });

        });

        //handle zoom
        addEventFilter(ScrollEvent.ANY, (ScrollEvent t) -> {
            handleZoom(t, drawer, back);
        });

        addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
//            if (event.isSecondaryButtonDown()) {
            Location viewPortLocation = drawer.getViewPortLocation();
            double scale = drawer.getScale();
            tooltipRect.translateXProperty().set(viewPortLocation.getX() + event.getSceneX());
            tooltipRect.translateYProperty().set(viewPortLocation.getY() + event.getSceneY());
            String text = getToolTipText(drawer, event);
            tooltip.textProperty().set(text);
            if (text.length() > 1) {
                tooltip.textProperty().set(text);
                tooltip.show(pane, event.getScreenX(), event.getScreenY());
            }
//            }
        });

        hvalueProperty().addListener((ov, n, xRatio) -> {
            handleHvalue(xRatio, drawer, back);
        });

        vvalueProperty().addListener((ov, n, yRatio) -> {
            handleVvalue(yRatio, drawer, back);
        });

        Image greenCarImage = new Image(R.class.getResourceAsStream("car-green.jpg"));
        Image blueCarImage = new Image(R.class.getResourceAsStream("car-blue.jpg"));
        for (long i = 0; i < carNum; i++) {
            DefinedSizeSpriteBasedEntity car = new DefinedSizeSpriteBasedEntity("" + i, (Math.random() > 0.5) ? greenCarImage : blueCarImage, DefinedSizeSpriteBasedEntity.SizeParameter.WIDTH, 1.7);
            boundingQuery.addToGroup("MOVING", "CARS", 10, 10, car.getRealHeight(), car.getRealWidth(), car);
        }

        setPrefWidth(800);
        setPrefHeight(600);
    }

    private void handleResize(Bounds newBounds) {
        //viewport is the amout of meters that we are viewing from the map
        drawer.setViewPortWidth(newBounds.getWidth());
        drawer.setViewPortHeight(newBounds.getHeight());
        refresh();
    }

    private void refresh() {
        //refresh hvalue and vvalue - will invoke listeners
        hvalueProperty().set(hvalueProperty().getValue() + 0.0001);
        hvalueProperty().set(hvalueProperty().getValue() - 0.0001);
    }

    private void handleVvalue(Number yRatio, SimpleDrawer drawer, MapCanvasLayer back) {
        double viewportY = yRatio.doubleValue() * (getContainerSize()[1] - getViewportBounds().getHeight());
        double viewportX = drawer.getViewPortLocation().getX();

        drawer.setViewPortLocation(viewportX, viewportY);
    }

    private void handleHvalue(Number xRatio, SimpleDrawer drawer, MapCanvasLayer back) {
        double viewportX = xRatio.doubleValue() * (getContainerSize()[0] - getViewportBounds().getWidth());
        double viewportY = drawer.getViewPortLocation().getY();

        drawer.setViewPortLocation(viewportX, viewportY);
    }

    private void handleZoom(ScrollEvent t, SimpleDrawer drawer, MapCanvasLayer back) {
        if (t.isControlDown()) {

            double scale = drawer.getScale() + t.getDeltaY() / 500.0;
            if (scale <= MIN_SCALE) {
                scale = MIN_SCALE;
            } else if (scale >= MAX_SCALE) {
                scale = MAX_SCALE;
            }

            drawer.setScale(scale);

            Point2D.Double mapSize = back.getGraphData().getBounds();

            super.setContainerSize(Convertor.worldToView(mapSize.x, scale), Convertor.worldToView(mapSize.y, scale));

            drawer.setViewPortWidth(getViewportBounds().getWidth());
            drawer.setViewPortHeight(getViewportBounds().getHeight());
            refresh();

            t.consume();
        }
    }

    private String getToolTipText(SimpleDrawer drawer, MouseEvent event) {
        double scale = drawer.getScale();
        double clickX = event.getSceneX();
        double clickY = event.getSceneY();
        Location viewPortLocation = drawer.getViewPortLocation();
        double getX = Convertor.viewToWorld(viewPortLocation.getX() + clickX, scale);
        double getY = Convertor.viewToWorld(viewPortLocation.getY() + clickY, scale);
        double viewPortWidth = drawer.getViewPortWidth();
        double viewPortHeight = drawer.getViewPortHeight();

        Vector entities = (Vector) boundingQuery.get("GRAPH", getX - TOOLTIP_RECT_WIDTH / 2, getX + TOOLTIP_RECT_WIDTH / 2, getY - TOOLTIP_RECT_WIDTH / 2, getY + TOOLTIP_RECT_WIDTH / 2);
        StringBuilder sb = new StringBuilder();
        for (Object entity : entities) {
            if (entity instanceof Edge) {
                Edge edge = (Edge) entity;
                sb.append("Edge " + edge.getId() + "\n");
                for (Map.Entry<String, String> entry : edge.getTags().entrySet()) {
                    sb.append(" " + entry.getKey() + ": " + entry.getValue() + "\n");
                }
            } else if (entity instanceof GraphPolygon) {
                GraphPolygon polygon = (GraphPolygon) entity;
                sb.append("Polygon " + (polygon).getId() + "\n");
                for (Map.Entry<String, String> entry : polygon.getTags().entrySet()) {
                    sb.append(" " + entry.getKey() + ": " + entry.getValue() + "\n");
                }
            } else if (entity instanceof AZVisVertex) {
                AZVisVertex vert = (AZVisVertex) entity;
                sb.append("Vertex " + (vert).getId() + "\n");
                for (Map.Entry<String, String> entry : vert.getTags().entrySet()) {
                    sb.append(" " + entry.getKey() + ": " + entry.getValue() + "\n");
                }
            }
        }
        return sb.toString();
    }

    private void init(String mapFilePath) {
//        graphData = new GraphReader().readGraph(mapFilePath);
        mapFilePath = "graph_try.txt";
        graphData = new NewGraphReader().readGraph(mapFilePath);
        images = new HashMap<>();
        images.put(new StringPair("building", "university"), new Image(R.class.getResourceAsStream("university.png")));
        images.put(new StringPair("building", "school"), new Image(R.class.getResourceAsStream("university.png")));
        images.put(new StringPair("building", "office"), new Image(R.class.getResourceAsStream("office.png")));
        images.put(new StringPair("highway", "traffic_signals"), new Image(R.class.getResourceAsStream("icons/traffic_signals.png")));
        images.put(new StringPair("amenity", "school"), new Image(R.class.getResourceAsStream("university.png")));
        images.put(new StringPair("amenity", "bank"), new Image(R.class.getResourceAsStream("icons/bank.png")));
        images.put(new StringPair("amenity", "atm"), new Image(R.class.getResourceAsStream("icons/bank.png")));
        images.put(new StringPair("amenity", "fuel"), new Image(R.class.getResourceAsStream("icons/fuel.png")));
        images.put(new StringPair("amenity", "parking"), new Image(R.class.getResourceAsStream("icons/parking.png")));
        images.put(new StringPair("amenity", "supermarket"), new Image(R.class.getResourceAsStream("icons/supermarket.png")));
        images.put(new StringPair("amenity", "hospital"), new Image(R.class.getResourceAsStream("icons/hospital.png")));
        images.put(new StringPair("amenity", "taxi"), new Image(R.class.getResourceAsStream("icons/taxi.png")));
        images.put(new StringPair("amenity", "telephone"), new Image(R.class.getResourceAsStream("icons/telephone.png")));
        images.put(new StringPair("amenity", "restaurant"), new Image(R.class.getResourceAsStream("icons/restaurant.png")));

        Image defaultImage = new Image(R.class.getResourceAsStream("building.png"));

        boundingQuery.createGroup("GRAPH", "NODES", false);
        boundingQuery.createGroup("SPRITES", "building", false);
        boundingQuery.createGroup("SPRITES", "icons", false);

        //insert all nodes and take care of special nodes that create icon sprites
        String[] iconsInterestKeys = {"amenity", "highway"};
        for (String vertexName : graphData.getVertexSet()) {
            AZVisVertex vertData = (AZVisVertex) graphData.getData(vertexName);
            boundingQuery.addToGroup("GRAPH", "NODES", vertData.getX(), vertData.getY(), 0.1, 0.1, vertData);
            //special tagged node should be drawn
            for (String key : iconsInterestKeys) {
                String tagValue = vertData.getTagValue(key);
                if (tagValue != null) {
                    Image img = images.get(new StringPair(key, tagValue));
                    if (img != null) {
                        DefinedSizeSpriteBasedEntity entity = new DefinedSizeSpriteBasedEntity("icon" + vertData.getId(), img, 10, 10);
                        entity.setLocation(new Location(vertData.getX(), vertData.getY()));
                        boundingQuery.addToGroup("SPRITES", "icons", vertData.getX(), vertData.getY(), 10, 10, entity);
                    }
                }
            }
        }

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
        boundingQuery.createGroup("GRAPH", "POLYGONS.leisure", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.landuse", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.building", false);
        boundingQuery.createGroup("GRAPH", "POLYGONS.defaultPolys", false);

        int i = 0;
        for (GraphPolygon poly : polys) {
            String key = poly.getTags().entrySet().iterator().next().getKey();
            if (boundingQuery.hasSubGroup("GRAPH", "POLYGONS." + key)) {
                boundingQuery.addToGroup("GRAPH", "POLYGONS." + key, poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            } else {
                boundingQuery.addToGroup("GRAPH", "POLYGONS.defaultPolys", poly.getCenter().x, poly.getCenter().y, poly.getWidth(), poly.getHeight(), poly);
            }

            //is building sprite drawing enabled
            boolean buildingSpriteOn = false;
            if (key.equals("building") && buildingSpriteOn) {
                double subScale = Math.sqrt(Math.abs(poly.getArea()));
                Image buildingImage = images.get(poly.getTags().get("building"));
                if (buildingImage == null) {
                    buildingImage = defaultImage;
                }
                double newW = subScale;
                double newH = (buildingImage.getHeight() / buildingImage.getWidth()) * newW;
//                if (newH > MAX_BUILDING_HEIGHT) {
//                    newW = newW / newH * MAX_BUILDING_HEIGHT;
//                    newH = MAX_BUILDING_HEIGHT;
//                }
                DefinedSizeSpriteBasedEntity entity = new DefinedSizeSpriteBasedEntity("" + i, buildingImage, newW, newH);
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
