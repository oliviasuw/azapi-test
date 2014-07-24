/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphData;

import java.util.ArrayDeque;
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

    private HashMap<String, Object> data = new HashMap<>();
    private SimpleWeightedGraph<String, String> graph = new SimpleWeightedGraph<>(String.class);

    //saves edges according to what will become their path descriptors
    private HashMap<String, Collection<String>> tagToEdges = new HashMap();
    
    public static GraphData parseGraph(String path){
        return (new GraphReader()).readGraph(path);
    }
    
    public Object getData(String name) {
        return data.get(name);
    }

    public void addVertex(String name, Object vertexData) {
        data.put(name, vertexData);
        graph.addVertex(name);
    }

    public void addEdge(String name, String from, String to, Object edgeData) {
        data.put(name, edgeData);

        try {
            graph.addEdge(from, to, name);
        } catch (Exception e) {
            System.out.print("");
        }

        //adding the edge type to the hashmap
        HashMap<String, String> edgeDataReal = (HashMap<String, String>) edgeData;
        String firstTag = edgeDataReal.values().iterator().next();
        if (tagToEdges.get(firstTag) == null) {
            tagToEdges.put(firstTag, new LinkedList<>());
        }
        tagToEdges.get(firstTag).add(name);

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

    /**
     * return w(u,v)
     * @param u
     * @param v
     * @return 
     */
    public double calcEdgeLength(String u, String v){
        AZVisVertex from = (AZVisVertex) this.getData(u);
        AZVisVertex to = (AZVisVertex) this.getData(v);
        return distance(from, to);
    }
    
    public ArrayDeque<Double> calcPathLength(ArrayDeque<String> path) {
        ArrayDeque<Double> lens = new ArrayDeque<>();
        AZVisVertex prev = null, curr;
        boolean flag = true;
        for (String currS : path) {
            if (flag) {
                prev = (AZVisVertex) this.getData(currS);
                flag = false;
            } else {
                curr = (AZVisVertex) this.getData(currS);
                lens.add(distance(prev, curr));
                prev = curr;
            }
        }
        return lens;
    }

    private double distance(AZVisVertex p1, AZVisVertex p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
