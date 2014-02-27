/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import java.util.LinkedList;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author Shlomi
 */
public class SimplePolygonImageDrawer implements PolygonImageDrawer {

    LinkedList<PolygonImageDrawer> drawers = new LinkedList<>();

    public SimplePolygonImageDrawer() {
        drawers.add(new BuildingImageDrawer());
    }

    @Override
    public boolean canDraw(GraphPolygon polygon) {
        for (PolygonImageDrawer drawer : drawers) {
            if (drawer.canDraw(polygon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale) {
        for (PolygonImageDrawer drawer : drawers) {
            if (drawer.canDraw(polygon)) {
                drawer.draw(canvas, graphData, polygon, scale);
                return;
            }
        }
    }

}
