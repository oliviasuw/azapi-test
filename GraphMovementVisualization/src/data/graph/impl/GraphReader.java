/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph.impl;

import graphmovementvisualization.AZVisVertex;
import graphmovementvisualization.GraphMovementVisualization;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shl
 */
public class GraphReader {

    public GraphData readGraph(String filePath) {
        GraphData graphData = new GraphData();
        try {
            Scanner in;
            in = new Scanner(new File(filePath));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                Scanner lineBreaker = new Scanner(line);
                String nextToken = lineBreaker.next();
                if (nextToken.equals("V")) {
                    String name = lineBreaker.next();
                    nextToken = lineBreaker.next();
                    Collection<Double> ints = parseVertex(lineBreaker, nextToken);
                    Iterator<Double> iterator = ints.iterator();
                    AZVisVertex vertexData = new AZVisVertex(name, iterator.next(), iterator.next());
                    graphData.addVertex(name, vertexData);
                } else if (nextToken.equals("E")) {
                    String from = lineBreaker.next();
                    String to = lineBreaker.next();
                    graphData.addEdge(from + " " + to, from, to, null);
                } else {
                    System.out.println("unsupported!");

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GraphMovementVisualization.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return graphData;
    }

    private Collection<Double> parseVertex(Scanner lineBreaker, String nextToken) {
        LinkedList<Double> ints = new LinkedList<>();
        while (lineBreaker.hasNext()) {
            if (nextToken.charAt(0) == '[') {
                nextToken = nextToken.substring(1);
                while (!(nextToken.charAt(nextToken.length() - 1) == ']')) {
                    ints.add(Double.parseDouble(nextToken));
                    nextToken = lineBreaker.next();
                }
                ints.add(Double.parseDouble(nextToken.substring(0, nextToken.length() - 1)));
            }
        }
        return ints;
    }
    
}
