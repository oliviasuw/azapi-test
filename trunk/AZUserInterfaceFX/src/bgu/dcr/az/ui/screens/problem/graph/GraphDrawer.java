/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Zovadi
 */
public class GraphDrawer extends Canvas {

    public GraphDrawer() {
        super(1024, 1024);
    }

    public GraphDrawer(double width, double height) {
        super(width, height);
    }

    public void draw(ProblemGraph pg) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.YELLOW);
        gc.fillRect(0, 0, getWidth(), getHeight());

        pg.getProblemGraph().getEdges().forEach(e -> e.draw(gc));

        pg.getProblemGraph().getVertices().forEach(v -> v.draw(gc));
    }
}
