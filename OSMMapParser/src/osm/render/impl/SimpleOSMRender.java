package osm.render.impl;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.LinkedList;
import mapparser.GraphWriter;
import osm.data.api.Bounds;
import osm.data.api.Map;
import osm.data.api.Node;
import osm.data.api.OSMObject;
import osm.render.api.MapRender;
import osm.render.api.OSMObjectRender;

/**
 *
 * @author Shl
 */
public class SimpleOSMRender implements MapRender {

    private static final double RAD_TO_METERS = 110600 / 180 * Math.PI;
    private double zoom;
    private Map map;
    private Point dimention;
    private double scale;
    private final LinkedList<OSMObjectRender> renders;

    public SimpleOSMRender() {
        renders = new LinkedList<>();
    }

    public void setScale(double scale) {
        this.scale = scale;
        zoom = RAD_TO_METERS * scale;
    }

    public Map getMap() {
        return map;
    }

    @Override
    public void addRender(OSMObjectRender render) {
        renders.addFirst(render);
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void render(Map map) {
        this.map = map;

        setup();

        //file write addition - does a lot of re-writes- need to put this at start       
        GraphWriter graphWriter = GraphWriter.getInstance();
        graphWriter.writeSize(dimention.x, dimention.y);

        //tags that are interested (only those will show up in nodes metadata - if existent)
        String tagsToAdd = "amenity highway shop name";
        String[] splitKeys = tagsToAdd.split(" ");
        for (Node node : map.getAvailableNodes()) {
            Point2D.Double p = globalTo2D_REAL(node);
            String tags = graphWriter.createTagString(node, tagsToAdd);
            graphWriter.writeVertex(String.valueOf(node.getID()), p.x, p.y, tags);
        }

        for (OSMObjectRender render : renders) {
            for (OSMObject w : map.getAvailableObjects()) {
                if (render.canRender(w)) {
                    render.render(w);
                }
            }
        }

        graphWriter.close();
    }

    @Override
    public void render(File outFile, Map map) throws Exception {
        render(map);
    }

    public Point mapSize2D() {
        double x = map.getBounds().getMaxX() - map.getBounds().getMinX();
        double y = map.getBounds().getMaxY() - map.getBounds().getMinY();

        return new Point((int) (x * zoom), (int) (y * zoom));
    }

    @Override
    public Point globalTo2D(Node node) {
        Point2D.Double real = globalTo2D_REAL(node);
        return new Point((int)real.x, (int)real.y);
//        double x = node.getLongitude() - map.getBounds().getMinX();
//        double y = node.getLatitude() - map.getBounds().getMinY();
//
//        return new Point((int) (x * zoom), dimention.y - (int) (y * zoom));
    }

    private void setup() {
//        dimention = mapSize2D();
        dimention = mapSize2D_REAL();

        for (OSMObjectRender render : renders) {
            render.setup(this);
        }
    }

    private double haversine_distance(Bounds bounds) {
        return haversine_distance(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    private double haversine_distance(double x1, double y1, double x2, double y2) {
        int R = 6371; //km
        double dLat = Math.toRadians(x2) - Math.toRadians(x1);
        double dLon = Math.toRadians(y2) - Math.toRadians(y1);
        double lat1 = Math.toRadians(x1);
        double lat2 = Math.toRadians(x2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public Point mapSize2D_REAL() {
//        double x = haversine_distance(map.getBounds().getMinX(), map.getBounds().getMinY(), map.getBounds().getMaxX(), map.getBounds().getMinY());
//        double y = haversine_distance(map.getBounds().getMinX(), map.getBounds().getMinY(), map.getBounds().getMinX(), map.getBounds().getMaxY());

        double x = ((map.getBounds().getMaxX() + 180) * 111 * 1000) - ((map.getBounds().getMinX() + 180) * 111 * 1000);
        double y = ((map.getBounds().getMaxY() + 90) * 111 * 1000) - ((map.getBounds().getMinY() + 90) * 111 * 1000);

        //*1000 to make it meters
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public Point2D.Double globalTo2D_REAL(Node node) {
        double longitude = node.getLongitude();
        double latitude = node.getLatitude();
        double y = (latitude + 90) * 111 * 1000;
        double x = (longitude + 180) * 111 * 1000;

        double mapx = (map.getBounds().getMinX() + 180) * 111 * 1000;
        double mapMaxY = (map.getBounds().getMaxY() + 90) * 111 * 1000;
        double mapMinY = (map.getBounds().getMinY() + 90) * 111 * 1000;

        double height = mapMaxY - mapMinY;

        return new Point2D.Double(x - mapx, height - (y - mapMinY));
    }

}
