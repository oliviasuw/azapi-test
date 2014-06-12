/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import osm.data.api.Node;
import osm.data.api.Tag;

/**
 *
 * @author Shl
 */
public class SimpleNode implements Node {

    private final double x;
    private final double y;
    private final long id;
    private final boolean visible;
    private final Set<Tag> flatTags;
    private final HashMap<String, Set<Tag>> tags;

    public SimpleNode(double x, double y, long id, boolean visible) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.visible = visible;

        this.flatTags = new HashSet<>();
        this.tags = new HashMap<>();
    }

    public void addTag(Tag tag) {
        flatTags.add(tag);
        
        if (!tags.containsKey(tag.getK())) {
            tags.put(tag.getK(), new HashSet<Tag>());
        }
        
        tags.get(tag.getK()).add(tag);
    }

    @Override
    public double getLongitude() {
        return x;
    }

    @Override
    public double getLatitude() {
        return y;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleNode other = (SimpleNode) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return this.id == other.id;
    }

}
