/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.dcr.api.problems.Problem;
import java.util.Collection;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author bennyl
 */
public abstract class ProblemGraphLayout {

    public final static double CIRCLE_RADIUS = 20;
    public final static double BORDER_SIZE = 2;
    public final static double EDGE_WIDTH = 3;
    public final static Font LABEL_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14);
    public final static Color OUTER_COLOR = Color.rgb(200, 200, 200);
    public final static Color INNER_COLOR = Color.rgb(50, 50, 50);;
    public final static Color TEXT_COLOR = Color.rgb(220, 220, 220);
    public final static Color EDGE_COLOR = INNER_COLOR;
    public final static Color GRAPH_BACKGROUND = Color.rgb(235, 235, 235);

    private final DoubleProperty widthProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();

    protected abstract void setup(Problem problem);

    protected abstract void setup(Problem problem, int var);

    public void setDimentions(double width, double height) {
        widthProperty.set(width);
        heightProperty.set(height);
        _setDimentions(width, height);
        System.out.println("Dims: " + width + " " + height);
    }

    protected abstract void _setDimentions(double width, double height);

    public void setProblem(Problem problem) {
        setup(problem);
    }

    public void setProblem(Problem problem, int var) {
        setup(problem, var);
    }

    public abstract Collection<Object> getVertices();

    public abstract Collection<Object> getNeighborsVertices(Object vertex);

    public abstract void step();

    public void steps(int num) {
        for (int i = 0; i < num; i++) {
            step();
        }
    }

    public void draw(GraphicsContext gc) {
        double width = widthProperty.get();
        double height = heightProperty.get();

        double minx = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;
        
        for (Object i : getVertices()) {
            Point u = getLocation(i);
            minx = Math.min(minx, u.x);
            maxx = Math.max(maxx, u.x);
            miny = Math.min(miny, u.y);
            maxy = Math.max(maxy, u.y);
        }
        
        double xt = (width / 2.0) - ((maxx - minx) / 2.0) - minx;
        double yt = (height / 2.0) - ((maxy - miny) / 2.0) - miny;
        
        double scale = Math.max((maxx - minx) > width ? width / (maxx - minx) : 1, (maxy - miny) > height ? height / (maxy - miny) : 1);

        for (Object i : getVertices()) {
            for (Object j : getNeighborsVertices(i)) {
                Point u = getLocation(i);
                Point v = getLocation(j);

                gc.setStroke(EDGE_COLOR);
                gc.setLineWidth(EDGE_WIDTH);
                gc.strokeLine((u.x + xt) * scale, (u.y + yt) * scale, (v.x + xt) * scale, (v.y + yt) * scale);
            }
        }

        for (Object i : getVertices()) {
            Point u = getLocation(i);
            double x = (u.x + xt) * scale;
            double y = (u.y + yt) * scale;
            gc.setFill(OUTER_COLOR);
            gc.fillOval(x - CIRCLE_RADIUS, y - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);

            gc.setFill(INNER_COLOR);
            gc.fillOval(x - CIRCLE_RADIUS + BORDER_SIZE, y - CIRCLE_RADIUS + BORDER_SIZE, (CIRCLE_RADIUS - BORDER_SIZE) * 2, (CIRCLE_RADIUS - BORDER_SIZE) * 2);

            gc.setFill(TEXT_COLOR);
            gc.setFont(LABEL_FONT);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("" + i, x, y);
        }

    }

    public abstract Point getLocation(Object var);

    protected static class Point {

        double x, y;
    }
}
