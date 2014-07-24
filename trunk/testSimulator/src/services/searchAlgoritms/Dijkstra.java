/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.searchAlgoritms;

import graphData.Graph;
import static java.lang.System.exit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This algorithm  find the shortest path between two
 * vertices in the graph.
 *
 * @author Roee
 */
public class Dijkstra extends PathGenerator {

 
    HashMap<String, Double> dist;
    HashMap<String, String> previous;
    Set<String> q;
    Double alt;
    
    public Dijkstra(Graph g) {      
        super(g);
        
        //init();
        
        
    }
    
    

    /*  this code will be modified and extended later, first i want to
     support basic functionality */
    @Override
    public ArrayDeque<String> generate(String src, String target) {
        return generatePath(src, target);
    }

    /**
     * for internal use, set distance to infinity, and previously to undefined
     */
    private void init() {
        for (String vertex : graph.getConnectedVertexSet()) {           
            dist.put(vertex, Double.MAX_VALUE);
            previous.put(vertex, "undefined");
            q.add(vertex);
            
        }
    }

    /**
     * generates a shortest path from a given vertex to another given vertex.
     *
     * @param start
     * @param goal
     * @return
     */
    public ArrayDeque<String> generatePath(String start, String goal) {
        this.dist = new HashMap<>();
        this.previous = new HashMap<>();       
        q = new HashSet();
        init();
        String u = start;
        dist.put(start, 0.0);
        while(!q.isEmpty()){
            q.remove(u);
            
            //if target is found
                if (u.equals(goal)){
                    ArrayDeque<String> stack = new ArrayDeque<>();
                    while(!previous.get(u).equals("undefined")){
                        stack.push(u);
                        u = previous.get(u);
                    }
                    stack.push(u);
                    return stack;
                }
            
            for (String v : this.graph.getAdjacentsOf(u)) {
                alt = dist.get(u) + weight(u,v);
                if (alt < dist.get(v)){
                    dist.put(v, alt);
                    previous.put(v, u);                 
                }                                        
            }            
            u = minDistVertex(q);
        }
        return new ArrayDeque<>();
    }

    /**
     * Weight function - maps each edge for a specific weight.
     * @param u
     * @param v
     * @return 
     */
    public double weight(String u, String v) {
        return length(u,v);
    }
    
    private double length(String u, String v) {
        return this.graph.distance(u, v);
    }

    
    private String minDistVertex(Set<String> q) {
        Double min = 0.0;
        String vertex = null;
        for(String u : q){
            if (dist.get(u) != -1.0){
                if(vertex == null){
                    vertex = u;
                    min = dist.get(u);
                } else{
                    if (dist.get(u)< min){
                        min=dist.get(u);
                        vertex=u;
                    }
                }
            }
        }
        return vertex;
    }
}

    
