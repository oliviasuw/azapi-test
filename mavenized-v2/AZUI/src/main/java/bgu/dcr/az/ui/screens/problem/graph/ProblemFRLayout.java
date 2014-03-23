/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author bennyl
 */
public class ProblemFRLayout extends ProblemJungLayout {

    private FRLayout frLayout;

    @Override
    public void step() {
        frLayout.step();
    }

    @Override
    protected AbstractLayout generateLayout(Graph g) {
        frLayout = new FRLayout(g);
        return frLayout;
    }

    @Override
    public String toString() {
        return "Fruchterman-Rheingold layout";
    }

}
