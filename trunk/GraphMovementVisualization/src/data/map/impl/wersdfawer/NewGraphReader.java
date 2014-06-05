/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class NewGraphReader {

    public GraphData readGraph(String filePath) {
        GraphData graphData = new GraphData();
        try {
            Scanner in;
            in = new Scanner(new File(filePath));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                Scanner lineBreaker = new Scanner(line);
                String nextToken = lineBreaker.next();
                if (nextToken.equals("S")) {
                    double sizeX = Double.parseDouble(lineBreaker.next());
                    double sizeY = Double.parseDouble(lineBreaker.next());
                    graphData.setBounds(new Point2D.Double(sizeX, sizeY));
                } else if (nextToken.equals("V")) {
                    String name = lineBreaker.next();
                    nextToken = lineBreaker.next();
                    Collection<Double> ints = parseVertex(lineBreaker, nextToken);
                    nextToken = lineBreaker.next();
                    HashMap<String, String> params = parseParams(lineBreaker, nextToken);
                    Iterator<Double> iterator = ints.iterator();
                    AZVisVertex vertexData = new AZVisVertex(name, iterator.next(), iterator.next(), params);
                    graphData.addVertex(name, vertexData);
                } else if (nextToken.equals("E")) {
                    String from = lineBreaker.next();
                    String to = lineBreaker.next();
                    String next = lineBreaker.next();
                    HashMap<String, String> params = parseParams(lineBreaker, next);
                    graphData.addEdge(from + " " + to, from, to, params);
                } else if (nextToken.equals("P")) {
                    String next = lineBreaker.next();
                    Collection<String> pNodes = parsePolygonNodes(lineBreaker, next);
                    next = lineBreaker.next();
                    HashMap<String, String> params = parseParams(lineBreaker, next);
                    graphData.addPolygon(pNodes, params);
                } else {
                    System.out.println("unsupported!");

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(R.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return graphData;
    }

    private Collection<Double> parseVertex(Scanner lineBreaker, String nextToken) {
        LinkedList<Double> ints = new LinkedList<>();
//        while (lineBreaker.hasNext()) {
        if (nextToken.charAt(0) == '[') {
            nextToken = nextToken.substring(1);
            while (!(nextToken.charAt(nextToken.length() - 1) == ']')) {
                ints.add(Double.parseDouble(nextToken));
                nextToken = lineBreaker.next();
            }
            ints.add(Double.parseDouble(nextToken.substring(0, nextToken.length() - 1)));
        }
//        }
        return ints;
    }

    private HashMap<String, String> parseParams(Scanner lineBreaker, String nextToken) {
        HashMap<String, String> params = new HashMap<>(5);

        while (nextToken != null) {
            if (nextToken.charAt(0) == '[') {
                nextToken = nextToken.substring(1);
                while (nextToken != null && !(nextToken.charAt(nextToken.length() - 1) == ']')) {
                    String[] split = nextToken.split("=");
                    params.put(split[0], split[1]);
                    nextToken = lineBreaker.next();
                }
                if (nextToken != null) {
                    String[] split = nextToken.substring(0, nextToken.length() - 1).split("=");
                    if (split.length >= 2) {
                        params.put(split[0], split[1]);
                    }
                }
            }
            if (lineBreaker.hasNext()) {
                nextToken = lineBreaker.next();
            } else {
                nextToken = null;
            }
        }
        return params;
    }

    private Collection<String> parsePolygonNodes(Scanner lineBreaker, String nextToken) {
        LinkedList<String> nodeIds = new LinkedList<>();
        while (!nextToken.equals("E")) {
            nodeIds.add(nextToken);
            nextToken = lineBreaker.next();
        }
        return nodeIds;
    }

}
