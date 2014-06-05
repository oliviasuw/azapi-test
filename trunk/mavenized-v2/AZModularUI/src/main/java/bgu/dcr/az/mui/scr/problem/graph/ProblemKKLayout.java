/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.problem.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author bennyl
 */
public class ProblemKKLayout extends ProblemJungLayout {

    private KKLayout kkLayout;

    @Override
    public void step() {
        kkLayout.step();
    }

    @Override
    protected AbstractLayout generateLayout(Graph g) {
        kkLayout = new KKLayout(g);
        return kkLayout;
    }

    @Override
    public String toString() {
        return "Kamada-Kawai layout";
    }

}
