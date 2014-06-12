/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.render.impl.way;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Collection;
import mapparser.GraphWriter;
import osm.data.api.Node;
import osm.data.api.Tag;
import osm.data.api.Way;
import osm.render.api.MapRender;
import osm.render.api.OSMObjectRender;

/**
 *
 * @author Shl
 */
public abstract class PolygonRender implements OSMObjectRender<Way> {

    private final Color color;
    private MapRender mapRender;

    public PolygonRender(Color color) {
        this.color = color;
    }

    @Override
    public void setup(MapRender mapRender) {
        this.mapRender = mapRender;
    }

    @Override
    public void render(Way way) {
        Polygon polygon = new Polygon();

        for (Node node : way.getNodes()) {
            Point p = mapRender.globalTo2D(node);
            polygon.addPoint(p.x, p.y);
        }

        //file write addition
        String interested = "building leisure landuse";
        for (String str : interested.split(" ")) {
            Collection<Tag> tags = way.getTags(str);
            if (tags != null) {
                Tag tag = tags.iterator().next();
                GraphWriter.getInstance().writePolygon(way.getNodes(), tag);
            }
        }
        
        
    }
}
