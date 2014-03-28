/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.dcr.api.problems.Problem;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.awt.Dimension;
import java.util.Collection;

/**
 *
 * @author bennyl
 */
public abstract class ProblemJungLayout extends ProblemGraphLayout {

    private Graph g;
    private AbstractLayout layout;

    @Override
    protected void _setDimentions(double width, double height) {
        if (layout != null) {
            width = Math.max(400, width);
            height = Math.max(400, height);
            layout.setSize(new Dimension((int) width, (int) height));
            layout.initialize();
            layout.reset();
            System.out.println("Laouy size updated to (" + width + ", " + height + ")");
        }
    }

    protected abstract AbstractLayout generateLayout(Graph g);

    @Override
    protected void setup(Problem problem) {
        g = new UndirectedSparseGraph();

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            g.addVertex(i);
        }

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            g.addVertex(i);
            for (int j = 0; j < i; j++) {
                if (problem.isConstrained(i, j)) {
                    g.addEdge("" + i + "_" + j, i, j);
                }
            }
        }

        layout = generateLayout(g);
        layout.setSize(new Dimension(100, 100));
        layout.initialize();
        layout.reset();
    }

    @Override
    protected void setup(Problem problem, int var) {
        g = new UndirectedSparseGraph();

        g.addVertex(var);
        problem.getNeighbors(var).stream().forEach(g::addVertex);

        problem.getNeighbors(var).stream().forEach(v -> {
            g.addEdge("" + var + "_" + v, var, v);
        });

        layout = generateLayout(g);

        layout.setSize(new Dimension(100, 100));
        layout.initialize();

        layout.reset();
    }

    @Override
    public Point getLocation(Object var) {
        Point p = new Point();
        p.x = layout.getX(var);
        p.y = layout.getY(var);
        return p;
    }

    @Override
    public Collection<Object> getVertices() {
        return g.getVertices();
    }

    @Override
    public Collection<Object> getNeighborsVertices(Object vertex) {
        return g.getNeighbors(vertex);
    }

}
