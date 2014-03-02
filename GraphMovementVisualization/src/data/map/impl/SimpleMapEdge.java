/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl;

import data.map.api.Map;
import data.map.api.MapEdge;
import data.map.api.MapEdgeProperties;
import data.map.api.MapNode;

/**
 *
 * @author Zovadi
 */
public class SimpleMapEdge implements MapEdge {

    private final long edgeId;
    private final MapNode startNode;
    private final MapNode endNode;
    private final MapEdgeProperties properties;

    public SimpleMapEdge(long edgeId, MapNode startNode, MapNode endNode) {
        this.edgeId = edgeId;
        this.startNode = startNode;
        this.endNode = endNode;
        this.properties = null;
    }

    public SimpleMapEdge(Map map, long edgeId, long startNode, long endNode) {
        this(edgeId, map.getNode(startNode), map.getNode(endNode));
    }

    @Override
    public long getEdgeId() {
        return edgeId;
    }

    @Override
    public MapNode getStartNode() {
        return startNode;
    }

    @Override
    public MapNode getEndNode() {
        return endNode;
    }

    @Override
    public MapEdgeProperties properties() {
        return properties;
    }

}
