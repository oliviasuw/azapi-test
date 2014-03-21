/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.api.prob.Problem;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author bennyl
 */
public abstract class ProblemGraphLayout {

    private final static double PADDING = 40;
    private final static double CIRCLE_RADIUS = 15;
    private final static double BORDER_SIZE = 2;
    private final Font LABEL_FONT = Font.getDefault();
    private final Color OUTER_COLOR = Color.BLACK;
    private final Color INNER_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = Color.RED;
    private final Color EDGE_COLOR = Color.AQUA;

    private Problem problem;

    private final DoubleProperty widthProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();

    protected abstract void setup();

    protected abstract void setup(int var);

    public void setDimentions(double width, double height) {
        widthProperty.set(width);
        heightProperty.set(height);
        _setDimentions(width, height);
    }

    protected abstract void _setDimentions(double width, double height);

    public void setProblem(Problem problem) {
        this.problem = problem;
        setup();
    }

    public void setProblem(Problem problem, int var) {
        this.problem = problem;
        setup(var);
    }

    public Problem getProblem() {
        return problem;
    }

    public abstract void step();

    public void steps(int num) {
        for (int i = 0; i < num; i++) {
            step();
        }
    }

    public void draw(GraphicsContext gc) {
        double width = widthProperty.get();
        double height = heightProperty.get();

        gc.setFill(Color.YELLOW);
        gc.fillRect(0, 0, width, height);

        double minx = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            Point u = getLocation(i);
            minx = Math.min(minx, u.x);
            maxx = Math.max(maxx, u.x);
            miny = Math.min(miny, u.y);
            maxy = Math.max(maxy, u.y);
        }

        double scalex = (width - PADDING * 2) / (maxx - minx);
        double scaley = (height - PADDING * 2) / (maxy - miny);

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            for (int j = 0; j < i; j++) {
                if (problem.isConstrained(i, j)) {
                    Point u = getLocation(i);
                    Point v = getLocation(j);

                    gc.setStroke(EDGE_COLOR);
                    final double ux = PADDING + (u.x - minx) * scalex;
                    final double uy = PADDING + (u.y - miny) * scaley;
                    final double vx = PADDING + (v.x - minx) * scalex;
                    final double vy = PADDING + (v.y - miny) * scaley;
                    gc.strokeLine(ux, uy, vx, vy);
                }
            }
        }

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            Point u = getLocation(i);
            double x = PADDING + (u.x - minx) * scalex;
            double y = PADDING + (u.y - miny) * scaley;
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

    public abstract Point getLocation(int var);

    protected static class Point {

        double x, y;
    }
}
