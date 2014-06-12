/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.api;

import java.awt.image.RenderedImage;
import java.io.File;
import osm.data.api.Map;

/**
 *
 * @author Shl
 */
public interface MapRender extends CoordinatesConverter {

    void addRender(OSMObjectRender render);
    
    double getScale();
    
    void render(Map map);

    void render(File outFile, Map map) throws Exception;
}
