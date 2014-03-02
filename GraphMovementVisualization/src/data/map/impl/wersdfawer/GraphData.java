/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.map.impl.wersdfawer;

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
    private LinkedList<GraphPolygon> polygons = new LinkedList<>();
    
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
        }
        catch (Exception e) {
            System.out.println("");
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
        
//    public Set<String> getOutgoingEdgesOf(String edgeName) {
//        return graph.outgoingEdgesOf(edgeName);
//    }
//    
//    public Set<String> getIncomingEdgesOf(String edgeName) {
//        return graph.incomingEdgesOf(edgeName);
//    }

    void addPolygon(Collection<String> pNodes, HashMap<String, String> params) {
       polygons.add(new GraphPolygon(pNodes, params));
    }

    public LinkedList<GraphPolygon> getPolygons() {
        return polygons;
    }

    

    
    
    
}
