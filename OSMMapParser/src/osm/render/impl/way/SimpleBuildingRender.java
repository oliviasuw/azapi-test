/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.impl.way;

import java.awt.Color;
import osm.data.api.OSMObject;
import osm.data.api.Way;

/**
 *
 * @author Shl
 */
public class SimpleBuildingRender extends PolygonRender {

    public SimpleBuildingRender() {
        super(Color.darkGray);
    }

    @Override
    public boolean canRender(OSMObject obj) {
        if (!(obj instanceof Way)) {
            return false;
        }
        return obj.getTags("building") != null;
    }
}
