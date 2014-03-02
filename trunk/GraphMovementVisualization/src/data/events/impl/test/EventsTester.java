/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl.test;

import bgu.dcr.az.vis.player.api.FramesStream;
import bgu.dcr.az.vis.player.impl.BasicOperationsFrame;
import bgu.dcr.az.vis.tools.Location;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import data.events.api.SimulatorEvent;
import data.events.impl.EventReader;
import data.events.impl.EventWriter;
import data.events.impl.MoveEvent;
import data.events.impl.Tick;
import data.events.impl.TickEvent;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shlomi
 */
public class EventsTester {

    Kryo kryo = new Kryo();
    private EventReader eventReader;
    private EventWriter eventWriter;
    private GraphData graphData;
    private Input input;
    private Output output;

    public EventsTester(GraphData graphData) {
        this.graphData = graphData;
        kryo.register(MoveEvent.class);
        kryo.register(TickEvent.class);
        kryo.register(SimulatorEvent.class);
        eventWriter = new EventWriter(kryo);
        eventReader = new EventReader(kryo);
        try {
            input = new Input(new FileInputStream("file.bin"));
//            output = new Output(new FileOutputStream("file.bin"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EventsTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void write() {
        writeTicksAndEvents(output);
    }

    public Tick read() {
        return eventReader.readNextTickFromInput(input);
    }

    private void writeTicksAndEvents(Output output) throws KryoException {
//        int spritesNum = 1;
//        String currEdge = "427182875 1775801775";
//        String nextEdge = "";
//        Random rand = new Random(System.currentTimeMillis());
//        for (int edges = 0; edges < 1000; edges++) {
//            Set<String> outgoing = graphData.getEdgesOf(currEdge.split(" ")[1]);
//            Object[] setArray = outgoing.toArray();
//            nextEdge = (String) setArray[rand.nextInt(outgoing.size())];
//            for (int i = 0; i <= 10; i = i + 5) {
//                eventWriter.writeTick(output, i);
//                int percentage = (int) (((double) i / 10) * 100);
//                for (int j = 0; j < spritesNum; j++) {
//                    if (Math.random() > 0) {
//                        System.out.println(currEdge);
//                        eventWriter.writeEvent(output, new MoveEvent(j, currEdge, percentage));
//                    }
//                }
//            }
//            currEdge = nextEdge;
//        }
//        output.close();
    }

    public void AddNewMovesFromTick(Tick tick, FramesStream stream) {

        Collection<SimulatorEvent> events = tick.getEvents();
        Set<String> edgeSet = graphData.getEdgeSet();
        BasicOperationsFrame frame = new BasicOperationsFrame();
        for (Object event : events) {
            if (event instanceof MoveEvent) {
                MoveEvent movee = (MoveEvent) event;
                Integer who = movee.getId();
                String from = movee.getFromNode();
                String to = movee.getToNode();

                double endPrecent = ((tick.getTickNum() - movee.getStartTick() + 1.0) / (movee.getEndTick() - movee.getStartTick() + 1.0)) * 100.0;
                double startPrecent = ((tick.getTickNum() - movee.getStartTick()) / (movee.getEndTick() - movee.getStartTick() + 1.0)) * 100.0;
//                if (!edgeSet.contains(edge)) {
//                    String[] split = edge.split(" ");
//                    Set<String> edgesof = graphData.getEdgesOf(split[1]);
//                    for (String meh : edgesof) {
//                        if (graphData.getEdgeTarget(meh).equals(split[0])) {
//                            edge = meh;
//                        }
//                    }
//
//                    String reversed = split[1] + " " + split[0];
//                }
                Location startLocation = translateToLocation(from, to, startPrecent);
                Location endLocation = translateToLocation(from, to, endPrecent);
                frame.directedMove(who, startLocation, endLocation);
            }
        }
            stream.writeFrame(frame);
    }

    private Location translateToLocation(String src, String target, Double precentage) {
//        String src = graphData.getEdgeSource(edgeName);
//        String target = graphData.getEdgeTarget(edgeName);
        AZVisVertex srcData = (AZVisVertex) graphData.getData(src);
        AZVisVertex targetData = (AZVisVertex) graphData.getData(target);
        double xsub = Math.abs(srcData.getX() - targetData.getX());
        double ysub = Math.abs(srcData.getY() - targetData.getY());
        double totalDistance = Math.sqrt(xsub * xsub + ysub * ysub);
        double distance = totalDistance * (precentage / 100D);
        double angle = Math.atan2(ysub, xsub);

        double newx = Math.abs(srcData.getX() + distance * Math.cos(angle));
        double newy = Math.abs(distance * Math.sin(angle) - srcData.getY());
        if (srcData.getX() > targetData.getX()) {
            newx = srcData.getX() - distance * Math.cos(angle);
        }
        if (srcData.getY() < targetData.getY()) {
            newy = Math.abs(distance * Math.sin(angle) + srcData.getY());
        }

        return new Location(newx, newy);
    }
}
