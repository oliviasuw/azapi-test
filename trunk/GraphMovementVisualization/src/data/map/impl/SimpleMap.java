/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl;

import data.map.api.Map;
import data.map.api.MapEdge;
import data.map.api.MapNode;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Zovadi
 */
public class SimpleMap implements Map {

    private final HashMap<Long, MapNode> nodes;
    private final HashMap<Long, MapEdge> edges;

    public SimpleMap() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
    }

    @Override
    public Collection<MapEdge> edges() {
        return edges.values();
    }

    @Override
    public MapEdge getEdge(long edgeId) {
        return edges.get(edgeId);
    }

    public void addEdge(MapEdge edge) {
        if (!edges.keySet().contains(edge.getEdgeId())) {
            addNode(edge.getStartNode());
            addNode(edge.getEndNode());
            edges.put(edge.getEdgeId(), edge);
        }
    }

    @Override
    public MapEdge getEdge(MapNode n1, MapNode n2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<MapNode> nodes() {
        return nodes.values();
    }

    @Override
    public MapNode getNode(long nodeId) {
        return nodes.get(nodeId);
    }

    public void addNode(MapNode node) {
        if (!nodes.keySet().contains(node.getNodeId())) {
            nodes.put(node.getNodeId(), node);
        }
    }

}
