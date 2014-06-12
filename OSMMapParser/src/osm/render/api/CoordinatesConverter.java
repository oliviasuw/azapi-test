/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.api;

import java.awt.Point;
import osm.data.api.Node;

/**
 *
 * @author Shl
 */
public interface CoordinatesConverter {
    
    Point globalTo2D(Node node);
}
