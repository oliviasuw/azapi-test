/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.api;

import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface Map {
    Bounds getBounds();
    
    Node getNode(long id);
    
    Way getWay(long id);
        
    Collection<Node> getAvailableNodes();
    
    Collection<Way> getAvailableWays();
        
    Collection<OSMObject> getAvailableObjects();
}
