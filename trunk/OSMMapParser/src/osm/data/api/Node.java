/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.api;

/**
 *
 * @author Shl
 */
public interface Node extends OSMObject {
    double getLongitude();
    
    double getLatitude();       
}
