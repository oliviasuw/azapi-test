/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Shl
 */
public class GraphData {

    public static final int QUAD_TREE_BOUNDS = 100000;

    private HashMap<String, Object> data = new HashMap<>();
    private SimpleWeightedGraph<String, String> graph = new SimpleWeightedGraph<>(String.class);

    //saves edges according to what will become their path descriptors
    private HashMap<String, Collection<String>> tagToEdges = new HashMap();

    private HashMap<String, Set<Object>> edgeToEntities = new HashMap<String, Set<Object>>();

    private LinkedList<GraphPolygon> polygons = new LinkedList<>();

    private double maxPolygonWidth = Double.MIN_VALUE;
    private double maxPolygonHeight = Double.MIN_VALUE;
    private double maxEdgeWidth = Double.MIN_VALUE;
    private double maxEdgeHeight = Double.MIN_VALUE;

    private double defaultScale = 0; //defaultscale in meters/pixels
    private Point2D.Double bounds;

    public Object getData(String name) {
        return data.get(name);
    }

    public void addVertex(String name, Object vertexData) {
        data.put(name, vertexData);
        graph.addVertex(name);
    }

    public void addEdge(String name, String from, String to, Object edgeData) {
        data.put(name, edgeData);
        System.out.println("from " + from + " to " + to);

        try {
            graph.addEdge(from, to, name);
        } catch (Exception e) {
            System.out.println("");
        }

        //adding the edge type to the hashmap
        HashMap<String, String> edgeDataReal = (HashMap<String, String>) edgeData;
        String highwayTag = edgeDataReal.get("highway");
        if (tagToEdges.get(highwayTag) == null) {
            tagToEdges.put(highwayTag, new LinkedList<>());
        }
        tagToEdges.get(highwayTag).add(name);

        AZVisVertex source = (AZVisVertex) this.getData(from);
//        tagToEdges.get(firstTag).put((float) source.getY(), (float) source.getX(), name);
        AZVisVertex target = (AZVisVertex) this.getData(to);
        double edgeWidth = Math.abs(target.getX() - source.getX());
        double edgeHeight = Math.abs(target.getY() - source.getY());
        if (edgeWidth > maxEdgeWidth) {
            maxEdgeWidth = edgeWidth;
        }
        if (edgeHeight > maxEdgeHeight) {
            maxEdgeHeight = edgeHeight;
        }

    }

    public Set<String> getVertexSet() {
        return graph.vertexSet();
    }

    public Set<String> getEdgeSet() {
        return graph.edgeSet();
    }

    public String getEdgeSource(String edgeName) {
        return graph.getEdgeSource(edgeName);
    }

    public String getEdgeTarget(String edgeName) {
        return graph.getEdgeTarget(edgeName);
    }

    public Set<String> getEdgesOf(String vertexName) {
        return graph.edgesOf(vertexName);
    }

    public HashMap<String, Collection<String>> getTagToEdge() {
        return tagToEdges;
    }

    void addPolygon(Collection<String> pNodes, HashMap<String, String> params) {
        GraphPolygon graphPolygon = new GraphPolygon(pNodes, params);
        graphPolygon.setCenter(this);
        polygons.add(graphPolygon);

        double height = graphPolygon.getHeight();
        double width = graphPolygon.getWidth();
        if (height > maxPolygonHeight) {
            maxPolygonHeight = height;
        }
        if (width > maxPolygonWidth) {
            maxPolygonWidth = width;
        }

    }

    public LinkedList<GraphPolygon> getPolygons() {
        return polygons;
    }

    public void setBounds(Point2D.Double bounds) {
        this.bounds = bounds;
    }

    public Point2D.Double getBounds() {
        return bounds;
    }

    public double getMaxPolygonWidth() {
        return maxPolygonWidth;
    }

    public double getMaxPolygonHeight() {
        return maxPolygonHeight;
    }

    public double getMaxEdgeWidth() {
        return maxEdgeWidth;
    }

    public double getMaxEdgeHeight() {
        return maxEdgeHeight;
    }

    public boolean addEntityToEdge(String edge, Object entity) {
        if (!graph.edgeSet().contains(edge)) {
            String[] split = edge.split(" ");
            edge = split[1] + " " + split[0];
            if (!graph.edgeSet().contains(edge)) {
                System.out.println("cant add entity! edgeset doesnt contain edge " + edge);
                return false;
            }
        }
        Set<Object> get = edgeToEntities.get(edge);
        if (get == null) {
            get = new HashSet<>();
            edgeToEntities.put(edge, get);
        }
        return get.add(entity);
    }

    public boolean removeEdgeEntity(String edge, Object entity) {
        if (!graph.edgeSet().contains(edge)) {
            String[] split = edge.split(" ");
            edge = split[1] + " " + split[0];
            if (!graph.edgeSet().contains(edge)) {
                System.out.println("cant get entities! edgeset doesnt contain edge " + edge);
            }
        }
        Set<Object> get = edgeToEntities.get(edge);
        if (get != null) {
            return get.remove(entity);
        }
        return false;
    }

    public Set<Object> getEdgeEntities(String edge) {
        if (!graph.edgeSet().contains(edge)) {
            String[] split = edge.split(" ");
            edge = split[1] + " " + split[0];
            if (!graph.edgeSet().contains(edge)) {
                System.out.println("cant get entities! edgeset doesnt contain edge " + edge);
            }
        }
        return edgeToEntities.get(edge);
    }

    public boolean hasEntity(String edge) {
        if (!graph.edgeSet().contains(edge)) {
            String[] split = edge.split(" ");
            edge = split[1] + " " + split[0];
            if (!graph.edgeSet().contains(edge)) {
                System.out.println("cant check if has entity! edgeset doesnt contain edge " + edge);
            }
        }
        Set<Object> get = edgeToEntities.get(edge);
        if (get != null) {
            return get.contains(edge);
        }
        return false;
    }

}
