/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl;

import data.map.api.MapNode;
import data.map.api.MapNodeProperties;

/**
 *
 * @author Zovadi
 */
public class SimpleMapNode implements MapNode {

    private final long nodeId;
    private final MapNodeProperties properties;

    public SimpleMapNode(long nodeId, MapNodeProperties properties) {
        this.nodeId = nodeId;
        this.properties = properties;
    }

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public MapNodeProperties properties() {
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapNode && ((MapNode) obj).getNodeId() == nodeId;
    }

    @Override
    public int hashCode() {
        return (int) nodeId; //To change body of generated methods, choose Tools | Templates.
    }

}
