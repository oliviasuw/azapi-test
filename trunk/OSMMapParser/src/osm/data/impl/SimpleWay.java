/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import osm.data.api.Map;
import osm.data.api.Node;
import osm.data.api.Tag;
import osm.data.api.Way;

/**
 *
 * @author Shl
 */
public class SimpleWay implements Way {

    private final Map map;
    private final long id;
    private final boolean visible;
    private final LinkedList<Long> nodesIds;
    private final Set<Tag> flatTags;
    private final HashMap<String, Set<Tag>> tags;

    public SimpleWay(Map map, long id, boolean visible) {
        this.map = map;
        this.id = id;
        this.visible = visible;

        nodesIds = new LinkedList<>();
        this.flatTags = new HashSet<>();
        this.tags = new HashMap<>();
    }

    public void addNodeId(Long nodeId) {
        nodesIds.add(nodeId);
    }

    public void addTag(Tag tag) {
        flatTags.add(tag);

        if (!tags.containsKey(tag.getK())) {
            tags.put(tag.getK(), new HashSet<Tag>());
        }

        tags.get(tag.getK()).add(tag);
    }

    @Override
    public Iterable<Node> getNodes() {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {

                return new Iterator<Node>() {
                    private Iterator<Long> iter = nodesIds.iterator();

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public Node next() {
                        return map.getNode(iter.next());
                    }

                    @Override
                    public void remove() {
                        iter.remove();
                    }
                };
            }
        };
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Collection<Tag> getTags() {
        return flatTags;
    }

    @Override
    public Collection<Tag> getTags(String key) {
        return tags.get(key);
    }

}
