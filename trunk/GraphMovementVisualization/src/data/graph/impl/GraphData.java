/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.graph.impl;

import java.util.HashMap;
import java.util.Set;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Shl
 */
public class GraphData {
    
    private HashMap<String, Object> data = new HashMap<>();
    private SimpleWeightedGraph<String, String> graph = new SimpleWeightedGraph<>(String.class);
    
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

        graph.addEdge(from, to, name);
    }

    public Iterable<String> getVertexSet() {
        return graph.vertexSet();
    }

    public Iterable<String> getEdgeSet() {
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
    
//    public Set<String> getOutgoingEdgesOf(String edgeName) {
//        return graph.outgoingEdgesOf(edgeName);
//    }
//    
//    public Set<String> getIncomingEdgesOf(String edgeName) {
//        return graph.incomingEdgesOf(edgeName);
//    }
    
    
    
}
