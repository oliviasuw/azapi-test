/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import osm.data.api.Bounds;
import osm.data.api.Map;
import osm.data.api.Node;
import osm.data.api.OSMObject;
import osm.data.api.Way;

/**
 *
 * @author Shl
 */
public class InMemoryMap implements Map {

    private Bounds bounds;
    private final HashMap<Long, Node> nodes;
    private final HashMap<Long, Way> ways;

    public InMemoryMap() {
        nodes = new HashMap<>();
        ways = new HashMap<>();
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public void addNode(Node node) {
        nodes.put(node.getID(), node);
    }

    public void addWay(Way way) {
        ways.put(way.getID(), way);
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public Node getNode(long id) {
        return nodes.get(id);
    }

    @Override
    public Way getWay(long id) {
        return ways.get(id);
    }


    @Override
    public Collection<Node> getAvailableNodes() {
        return nodes.values();
    }

    @Override
    public Collection<Way> getAvailableWays() {
        return ways.values();
    }

    @Override
    public Collection<OSMObject> getAvailableObjects() {
        LinkedList<OSMObject> objs = new LinkedList<>();
        objs.addAll(getAvailableNodes());
        objs.addAll(getAvailableWays());
        return objs;
    }
}
