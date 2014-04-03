/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer;

import bgu.dcr.az.vis.tools.Location;
import com.bbn.openmap.util.quadtree.QuadTree;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
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
//    private HashMap<String, QuadTree> tagToEdges = new HashMap<String, QuadTree>();
    private HashMap<String, Collection<String>> tagToEdges = new HashMap();

    
    private LinkedList<GraphPolygon> polygons = new LinkedList<>();

//    private QuadTree polygons = new QuadTree(0, QUAD_TREE_BOUNDS, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS);
    private double maxPolygonWidth = Double.MIN_VALUE;
    private double maxPolygonHeight = Double.MIN_VALUE;
    private double maxEdgeWidth = Double.MIN_VALUE;
    private double maxEdgeHeight = Double.MIN_VALUE;

    private double defaultScale = 0; //defaultscale in meters/pixels
    private Point2D.Double bounds;

    public Object getData(String name) {
        return data.get(name);
    }

//        
//    public Object addData(String name, Object toPut) {
//        return data.put(name, toPut);
//    }
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
        String firstTag = edgeDataReal.values().iterator().next();
        if (tagToEdges.get(firstTag) == null) {
            tagToEdges.put(firstTag, new LinkedList<>());
//            tagToEdges.put(firstTag, new QuadTree(0, QUAD_TREE_BOUNDS, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS));
        }
        tagToEdges.get(firstTag).add(name);

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

//    public Set<String> getOutgoingEdgesOf(String edgeName) {
//        return graph.outgoingEdgesOf(edgeName);
//    }
//    
//    public Set<String> getIncomingEdgesOf(String edgeName) {
//        return graph.incomingEdgesOf(edgeName);
//    }
    void addPolygon(Collection<String> pNodes, HashMap<String, String> params) {
        GraphPolygon graphPolygon = new GraphPolygon(pNodes, params);
        graphPolygon.setCenter(this);
        polygons.add(graphPolygon);

//        polygons.put((float) graphPolygon.getCenter().y, (float) graphPolygon.getCenter().x, graphPolygon);
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

//    public QuadTree getEdgeQuadTree() {
//        return edgeQuadTree;
//    }
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
    
    

}
