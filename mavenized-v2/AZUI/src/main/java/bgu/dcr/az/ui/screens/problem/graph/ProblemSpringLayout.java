/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author bennyl
 */
public class ProblemSpringLayout extends ProblemJungLayout {

    private SpringLayout springLayout;

    @Override
    public void step() {
        springLayout.step();
    }

    @Override
    protected AbstractLayout generateLayout(Graph g) {
        springLayout = new SpringLayout(g);
        springLayout.setStretch(0.01);
        return springLayout;
    }

    @Override
    public String toString() {
        return "Force-directed spring-embedder layout";
    }

}
