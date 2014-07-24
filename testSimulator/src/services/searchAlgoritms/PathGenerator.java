/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.searchAlgoritms;

import graphData.Graph;
import java.util.ArrayDeque;

/**
 *
 * @author Eran
 */
public abstract class PathGenerator {

    protected final Graph graph;

    public PathGenerator(Graph g) {
        this.graph = g;
    }

    /**
     * generate a path from the the given vertices (can handle two vertices at
     * most)
     *
     * @param src
     * @param target
     * @return
     */
    public abstract ArrayDeque<String> generate(String  src, String target);

}
