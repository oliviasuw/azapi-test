/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zovadi
 */
public class SpringLayout {

    private final double c0;
    private final double c1;
    private final double l;
    private final double delta;

    private final Map<Vertex, Point> forces;

    public SpringLayout(double c0, double c1, double l, double delta) {
        this.c0 = c0;
        this.c1 = c1;
        this.l = l;
        this.delta = delta;
        this.forces = new HashMap<>();
    }

    private Point calculateForce1(Vertex u, Vertex v) {
        double dist2 = (u.getX() - v.getX()) * (u.getX() - v.getX()) + (u.getY() - v.getY()) * (u.getY() - v.getY());
        double dist = Math.sqrt(dist2);
        Point f = new Point();
        f.x = dist == 0 ? 0 : c0 * (v.getX() - u.getX()) / dist2;
        f.y = dist == 0 ? 0 : c0 * (v.getY() - u.getY()) / dist2;

        return f;
    }

    private Point calculateForce2(Vertex u, Vertex v) {
        double dist2 = (u.getX() - v.getX()) * (u.getX() - v.getX()) + (u.getY() - v.getY()) * (u.getY() - v.getY());
        double dist = Math.sqrt(dist2);
        Point f = new Point();
        f.x = dist == 0 ? 0 : -c1 * (dist - l) * (v.getX() - u.getX()) / dist;
        f.y = dist == 0 ? 0 : -c1 * (dist - l) * (v.getY() - u.getY()) / dist;

        return f;
    }

    public void executeStep(ProblemGraph pg) {
        forces.clear();

        for (Vertex u : pg.getProblemGraph().getVertices()) {
            Point force = new Point();
            force.x = 0;
            force.y = 0;
            for (Vertex v : pg.getProblemGraph().getVertices()) {
                Point af1 = calculateForce1(u, v);
                force.x += af1.x;
                force.y += af1.y;

                if (pg.getProblemGraph().getNeighbors(u).contains(v)) {
                    Point af2 = calculateForce2(u, v);
                    force.x += af2.x;
                    force.y += af2.y;
                }
            }
            forces.put(u, force);
        }

        forces.entrySet().forEach(e -> {
            Vertex v = e.getKey();
            Point f = e.getValue();
            v.setX(ensure(v.getX() + delta * f.x, 50, 900));
            v.setY(ensure(v.getY() + delta * f.y, 50, 600));
        });
    }

    public void executeSteps(ProblemGraph pg, int iterations) {
        for (int i = 0; i < iterations; i++) {
            executeStep(pg);
        }
    }

    private double ensure(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static class Point {

        double x, y;
    }
}
