/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.problem.graph;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Zovadi
 */
public class GraphDrawer extends Canvas {

    public GraphDrawer() {
        super(800, 400);
    }

    public GraphDrawer(double width, double height) {
        super(width, height);
    }

    public void draw(ProblemGraphLayout gl) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(ProblemGraphLayout.GRAPH_BACKGROUND);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gl.draw(gc);
    }

    public void setDimentions(double width, double height) {
        setWidth(width);
        setHeight(height);
    }
}
