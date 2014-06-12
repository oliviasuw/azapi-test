/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl.test;

import bgu.dcr.az.vis.tools.StringPair;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import data.events.impl.EventWriter;
import data.events.impl.MoveEvent;
import data.map.impl.wersdfawer.GraphData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Shl
 */
public class MyEventWriter {

    private HashMap<String, StringPair> carToEdge = new HashMap<String, StringPair>();
    GraphData graphData;
    Random rand = new Random();
    EventWriter eventWriter;

    public MyEventWriter(GraphData graphData, EventWriter eventWriter) {
        this.graphData = graphData;
        this.eventWriter = eventWriter;
    }

    
    public void writeTicksAndEvents(Output output) throws KryoException {
        int spritesNum = 1000;
        int wayLength = 100;
        Set<String> edgeSet = graphData.getEdgeSet();
        String[] a = new String[edgeSet.size()];
        String[] edgeArr = edgeSet.toArray(a);
        
        for (int i = 0; i < spritesNum; i++) {
            int randEdge = rand.nextInt(edgeArr.length);
            String[] selectedEdge = edgeArr[i].split(" ");
            carToEdge.put(String.valueOf(i), new StringPair(selectedEdge[0], selectedEdge[1]));
        }

//        String currEdge = "1107293662 1107288392";
//        String nextEdge = "";
        for (int turns = 0; turns < wayLength; turns++) {
            eventWriter.writeTick(output, turns);
            for (int carId = 0; carId < spritesNum; carId++) {
                StringPair get = carToEdge.get(String.valueOf(carId));
                Set<String> outgoing = graphData.getEdgesOf(get.getSecond());
                Object[] setArray = outgoing.toArray();
                String nextEdge = (String) setArray[rand.nextInt(outgoing.size())];
                String[] split = nextEdge.split(" ");
                String s = get.getSecond().equals(split[0]) ? split[0] : split[1];
                String t = get.getSecond().equals(split[0]) ? split[1] : split[0];
                eventWriter.writeEvent(output, new MoveEvent(carId, t, s, 0, 100));
                get.setFirst(get.getSecond());
                get.setSecond(split[1]);
            }
        }
        output.close();
        
    }
    
}
