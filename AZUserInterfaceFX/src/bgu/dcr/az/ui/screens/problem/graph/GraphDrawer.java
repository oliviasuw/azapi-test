/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.api.prob.Problem;
import static bgu.dcr.az.ui.screens.problem.graph.Edge.EDGE_COLOR;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Zovadi
 */
public class GraphDrawer extends Canvas {

    private final static double PADDING = 40; 
    
    public GraphDrawer() {
        super(800, 400);
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

    public void draw(Problem p, Eades84Layout l) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.YELLOW);
        gc.fillRect(0, 0, getWidth(), getHeight());

        double minx = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            Point3 u = l.getPos("" + i);
            minx = Math.min(minx, u.x);
            maxx = Math.max(maxx, u.x);
            miny = Math.min(miny, u.y);
            maxy = Math.max(maxy, u.y);
        }

        double scalex = (getWidth() - PADDING * 2) / (maxx - minx);
        double scaley = (getHeight() - PADDING * 2) / (maxy - miny);

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < i; j++) {
                if (p.isConstrained(i, j)) {
                    Point3 u = l.getPos("" + i);
                    Point3 v = l.getPos("" + j);

                    gc.setStroke(EDGE_COLOR);
                    final double ux = PADDING + (u.x - minx) * scalex;
                    final double uy = PADDING + (u.y - miny) * scaley;
                    final double vx = PADDING + (v.x - minx) * scalex;
                    final double vy = PADDING + (v.y - miny) * scaley;
                    gc.strokeLine(ux, uy, vx, vy);
                }
            }
        }

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            Point3 u = l.getPos("" + i);
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

    private final static double CIRCLE_RADIUS = 15;
    private final static double BORDER_SIZE = 2;
    private final Font LABEL_FONT = Font.getDefault();
    private final Color OUTER_COLOR = Color.BLACK;
    private final Color INNER_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = Color.RED;

}
