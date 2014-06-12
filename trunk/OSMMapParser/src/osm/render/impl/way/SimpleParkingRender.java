/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.impl.way;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import osm.data.api.OSMObject;
import osm.data.api.Tag;
import osm.data.api.Way;

/**
 *
 * @author Shl
 */
public class SimpleParkingRender extends PolygonRender {

    public SimpleParkingRender() {
        super(Color.pink);
    }

    @Override
    public boolean canRender(OSMObject obj) {
        if (!(obj instanceof Way)) {
            return false;
        }
        
        Collection<Tag> tags = obj.getTags("amenity");
        if (tags != null) {
            for (Iterator<Tag> it = tags.iterator(); it.hasNext();) {
                Tag tag = it.next();
                if (tag.getV().equals("parking")) {
                    return true;
                }
            }
        }
        return false;
    }
}
