/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author bennyl
 */
public class ProblemISOMLayout extends ProblemJungLayout {

    private ISOMLayout isomLayout;

    @Override
    public void step() {
        isomLayout.step();
    }

    @Override
    protected AbstractLayout generateLayout(Graph g) {
        isomLayout = new ISOMLayout(g);
        return isomLayout;
    }

    @Override
    public String toString() {
        return "Meyer's Self-Organizing Map layout";
    }

}
