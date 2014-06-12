/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.impl.way.road;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import mapparser.GraphWriter;
import osm.data.api.Node;
import osm.data.api.OSMObject;
import osm.data.api.Tag;
import osm.data.api.Way;
import osm.render.api.OSMObjectRender;
import osm.render.api.MapRender;

/**
 *
 * @author Shl
 */
public class SimpleRoadRender implements OSMObjectRender<Way> {

    public static final String ROAD_KEY = "highway";
    private MapRender mapRender;

    public SimpleRoadRender() {

    }

    @Override
    public boolean canRender(OSMObject obj) {
        if (!(obj instanceof Way)) {
            return false;
        }
        return obj.getTags(ROAD_KEY) != null;
    }

    @Override
    public void setup(MapRender mapRender) {
        this.mapRender = mapRender;
    }

    @Override
    public void render(Way way) {
        Iterator<Node> nodeIt = way.getNodes().iterator();

        Node curr = nodeIt.next(); //first node 
        if (curr == null) {
            throw new RuntimeException("An empty way accepted.");
        }
        Point p = mapRender.globalTo2D(curr);

        while (nodeIt.hasNext()) {
            Node old = curr;
            curr = nodeIt.next();
            p = mapRender.globalTo2D(curr);

            //file write addition
            String interested = ROAD_KEY + " name:en oneway lanes";
            Collection<Tag> tags = way.getTags(ROAD_KEY);
            Tag roadTag = tags.iterator().next();
            String tagString = GraphWriter.getInstance().createTagString(way, interested);
            if (!roadTag.getV().equals("footway")) {
                GraphWriter.getInstance().writeEdge("", String.valueOf(old.getID()), String.valueOf(curr.getID()), tagString);
            }
        }
    }

}
