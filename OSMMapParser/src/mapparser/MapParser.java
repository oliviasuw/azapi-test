/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapparser;

import java.io.File;
import osm.data.api.Map;
import osm.data.impl.InMemoryMap;
import osm.data.impl.MapUtils;
import osm.render.impl.SimpleOSMRender;
import osm.render.impl.way.SimpleBuildingRender;
import osm.render.impl.way.SimpleGrassRender;
import osm.render.impl.way.SimpleParkingRender;
import osm.render.impl.way.road.SimpleRoadRender;

/**
 *
 * @author Shl
 */
public class MapParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String path = "manhattan";
        Map map = (InMemoryMap) MapUtils.readMap(new File(path));
        SimpleOSMRender mapRender = new SimpleOSMRender();
        //why do we need this if this is real size???
        mapRender.setScale(50);
        mapRender.addRender(new SimpleBuildingRender());
        mapRender.addRender(new SimpleRoadRender());
        mapRender.addRender(new SimpleGrassRender());
        mapRender.addRender(new SimpleParkingRender());
        mapRender.render(map);
    }

}
