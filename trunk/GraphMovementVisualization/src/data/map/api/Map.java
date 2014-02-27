/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.api;

import java.util.Collection;

/**
 *
 * @author Zovadi
 */
public interface Map {

    Collection<MapEdge> edges();

    MapEdge getEdge(long edgeId);

    MapEdge getEdge(MapNode n1, MapNode n2);

    Collection<MapNode> nodes();

    MapNode getNode(long nodeId);
}
