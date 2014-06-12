/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.api;

import java.awt.Graphics2D;
import osm.data.api.OSMObject;

/**
 *
 * @author Shl
 */
public interface OSMObjectRender<T extends OSMObject> {
    
    boolean canRender(OSMObject obj);
    
    void setup(MapRender mapRender);

    void render(T obj);
    
}
