/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.api;

import java.util.Collection;
import osm.data.api.Tag;

/**
 *
 * @author Shl
 */
public interface TagRegistry {
    void registerRender(Tag tag, OSMObjectRender drawer);
    
    OSMObjectRender getRender(Tag tag);
    
    Collection<OSMObjectRender> getAvailableRenders();
}
