/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.searchAlgoritms;

import graphData.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This algorithm doesn't necessarily find the shortest path between two
 * vertices in the graph.
 *
 * @author Eran
 */
public class RandomDFS extends PathGenerator {

    Random rand = new Random(18);

    public enum Color {

        Black, Grey, White
    };
    HashMap<String, Color> color;

    public RandomDFS(Graph g) {
        super(g);
        this.color = new HashMap<>();
        resetColors();
    }

    /*  this code will be modified and extended later, first i want to
     support basic functionality */
    @Override
    public ArrayDeque<String> generate(String src, String target) {
        return generatePath(src, target);
    }

    /**
     * for internal use, reset the color of each vertex in the graph to White
     */
    private void resetColors() {
        for (String vertex : graph.getConnectedVertexSet()) {
            color.put(vertex, Color.White);
        }
    }

    /**
     * generates a path from a given vertex to another given vertex.
     *
     * @param start
     * @param goal
     * @return
     */
    public ArrayDeque<String> generatePath(String start, String goal) {
        resetColors();
        ArrayDeque<String> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (node.equals(goal)) {
                stack.push(node);
                ArrayDeque<String> reverse = new ArrayDeque<>();
                while (!stack.isEmpty()) {
                    reverse.push(stack.pop());
                }
                return reverse;
            }
            color.put(node, Color.Grey);
            String whiteChild = getWhiteChild(node);
            if (whiteChild == null) {
                color.put(node, Color.Black);
            } else {
                stack.push(node);
                stack.push(whiteChild);
            }
        }
        return new ArrayDeque<>();
    }

    /**
     * return a random, white-colored adjacent of 'node'.
     * @param node
     * @return 
     */
    String getWhiteChild(String node) {
//        if (this.graph.getEdgesOf(node).isEmpty()) {
        if (this.graph.getAdjacentsOf(node).isEmpty()) {
            return null;
        } else {
            ArrayList<String> children = new ArrayList<>();
//            for (String edge : this.graph.getEdgesOf(node)) {
//                String id = getEdgePartner(edge, node);
            for (String id : this.graph.getAdjacentsOf(node)) { //instead of the two lines above
                if (color.get(id) == Color.White && this.graph.calcEdgeLength(node, id) > 0) {
                    children.add(id);
                }
            }
            if (children.isEmpty()) {
                return null;
            }
            int nextInt = rand.nextInt(children.size());
            return children.get(nextInt);
        }

    }
    
}
