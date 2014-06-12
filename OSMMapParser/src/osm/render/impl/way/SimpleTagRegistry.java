/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.impl.way;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import osm.data.api.Tag;
import osm.render.api.OSMObjectRender;
import osm.render.api.TagRegistry;

/**
 *
 * @author Shl
 */
public class SimpleTagRegistry implements TagRegistry {
    private final HashMap<Tag, OSMObjectRender> registry;
    private final LinkedList<OSMObjectRender> renders;
    
    public SimpleTagRegistry() {
        registry = new HashMap<>();
        renders = new LinkedList<>();
    }
    
    @Override
    public void registerRender(Tag tag, OSMObjectRender render) {
        registry.put(tag,render);
        renders.addLast(render);
    }

    @Override
    public OSMObjectRender getRender(Tag tag) {
        return registry.get(tag);
    }

    @Override
    public Collection<OSMObjectRender> getAvailableRenders() {
        return renders;
    }
    
}
