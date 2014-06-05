/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.problem.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author bennyl
 */
public class ProblemCircleLayout extends ProblemJungLayout {

    @Override
    protected AbstractLayout generateLayout(Graph g) {
        return new CircleLayout(g);
    }

    @Override
    public void step() {
    }

    @Override
    public String toString() {
        return "Circle layout";
    }

}
