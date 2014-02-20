/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph.impl;

import graphmovementvisualization.AZVisVertex;
import java.util.Collection;
import java.util.HashMap;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Shl
 */
public class AZVisGraph extends SimpleWeightedGraph<AZVisVertex, DefaultEdge> {

    HashMap<String, AZVisVertex> nameToVert = new HashMap<>();

    public AZVisGraph() {
        super(DefaultEdge.class);
        
       
        
    }

    public AZVisGraph(EdgeFactory<AZVisVertex, DefaultEdge> ef) {
        super(ef);
    }

    @Override
    public boolean addVertex(AZVisVertex vert) {
        nameToVert.put(vert.getName(), vert);
        return super.addVertex(vert);
    }

    public DefaultEdge addEdgeByNames(String source, String dest) {
        AZVisVertex srcVert = nameToVert.get(source);
        AZVisVertex destVert = nameToVert.get(dest);
        if (srcVert != null && destVert != null) {
            return super.addEdge(srcVert, destVert);
        } else {
            System.out.println("no such vertex!!!");
            return null;
        }
    }

    public DefaultEdge getEdgeByNames(String source, String dest) {
        AZVisVertex srcVert = nameToVert.get(source);
        AZVisVertex destVert = nameToVert.get(dest);
        if (srcVert != null && destVert != null) {
            return super.getEdge(srcVert, destVert);
        }
        else {
            System.out.println("no such vertex!!!");
            return null;
        }
    }
   

}
