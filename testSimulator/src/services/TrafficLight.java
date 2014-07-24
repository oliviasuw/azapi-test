/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import statistics.Utility;

/**
 *
 * @author Eran
 */
class TrafficLight {

    private static final Random rand = Utility.rand;

    private int greenTime, offset;
    private LinkedList<String> adj;
    

    /**
     * The traffic lights interval is [red, green, red, green,...]. offset is
     * some random value in the first interval [red,green] in order to determine
     * the initial color of the traffic light, and the remaining time until it
     * changes it's color.
     *
     * @param green
     * @param red
     */
    public TrafficLight(int green, HashSet<String> adjacents) {
        if(adjacents.isEmpty()){
//            System.out.println("dafuq??!!");
            return;
        }
        
        this.adj = new LinkedList<>(adjacents);
        this.greenTime = green;
        this.offset = rand.nextInt(adj.size());
    }

    public String getCurrent(int tick) {
        int intervalSize = greenTime*adj.size();
        int timeElapsed = greenTime*offset + tick;
        return this.adj.get((timeElapsed % intervalSize) / greenTime);
    }
}
