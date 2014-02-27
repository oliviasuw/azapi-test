/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import data.graph.impl.GraphData;
import data.graph.impl.GraphPolygon;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author Shlomi
 */
public interface PolygonImageDrawer {

    public boolean canDraw(GraphPolygon polygon);

    public void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale);

}
