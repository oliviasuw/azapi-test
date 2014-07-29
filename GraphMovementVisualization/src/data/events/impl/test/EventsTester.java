/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl.test;

import bgu.dcr.az.vis.newplayer.SimplePlayer;
import bgu.dcr.az.vis.player.impl.BasicOperationsFrame;
import bgu.dcr.az.vis.player.impl.entities.ParkingLotEntity;
import bgu.dcr.az.vis.tools.Location;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import data.events.api.SimulatorEvent;
import data.events.impl.EventReader;
import data.events.impl.EventWriter;
import data.events.impl.MoveEvent;
import data.events.impl.ParkingEvent;
import data.events.impl.Tick;
import data.events.impl.TickEvent;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.Edge;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
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
    public static int THRESHOLD = 2;

    public EventsTester(GraphData graphData) {
        this.graphData = graphData;
        kryo.register(MoveEvent.class);
//        kryo.register(TurnEvent.class);
        kryo.register(TickEvent.class);
        kryo.register(SimulatorEvent.class);
        kryo.register(ParkingEvent.class);
        
//        eventWriter = new EventWriter(kryo);
        eventReader = new EventReader(kryo);
        try {
            output = new Output(new FileOutputStream("file.bin"));
//            MyEventWriter myEV = new MyEventWriter(graphData, eventWriter);
//            myEV.writeTicksAndEvents(output);
            input = new Input(new FileInputStream("file_1a_2000t.bin"));
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
        int spritesNum = 100;
        String currEdge = "1107293662 1107288392";
        String nextEdge = "";
        Random rand = new Random(System.currentTimeMillis());
        for (int edges = 0; edges < 1000; edges++) {
            Set<String> outgoing = graphData.getEdgesOf(currEdge.split(" ")[1]);
            Object[] setArray = outgoing.toArray();
            nextEdge = (String) setArray[rand.nextInt(outgoing.size())];
            for (int i = 0; i < 10; i = i + 5) {
                eventWriter.writeTick(output, i);
                int percentage = (int) (((double) i / 10) * 100);
                int nextPer = (int) (((double) (i + 5) / 10) * 100);
                for (int j = 0; j < spritesNum; j++) {
                    if (Math.random() > 0) {
//                        System.out.println(currEdge);
                        String[] split = currEdge.split(" ");
                        eventWriter.writeEvent(output, new MoveEvent(j, split[0], split[1], percentage, nextPer));
                    }
                }
            }
            currEdge = nextEdge;
        }
        output.close();
    }

    public void AddNewMovesFromTick(Tick tick, SimplePlayer player, GroupBoundingQuery boundingQuery) {

        Collection<SimulatorEvent> events = tick.getEvents();
        Set<String> edgeSet = graphData.getEdgeSet();
        BasicOperationsFrame frame = new BasicOperationsFrame();
        for (Object event : events) {
            if (event instanceof MoveEvent) {
                MoveEvent movee = (MoveEvent) event;
                Integer who = movee.getId();
                String from = movee.getFromNode();
                String to = movee.getToNode();
                double startPrecent = movee.getStartPrecent();
                double endPrecent = movee.getEndPrecent();
                Location startLocation = translateToLocation(from, to, startPrecent);
                Location endLocation = translateToLocation(from, to, endPrecent);
                frame.directedMove(who, startLocation, endLocation);
                maintainDynamicColoring(from, to, startPrecent, who, boundingQuery, endPrecent);
            }
            if (event instanceof ParkingEvent) {
                ParkingEvent parke = (ParkingEvent) event;
                ParkingLotEntity ple = (ParkingLotEntity)boundingQuery.getById("parking" + parke.getParkNodeId());
                if (ple != null && parke.getInOut() == ParkingEvent.InOut.IN) {
                    ple.addToData(parke.getPrecentage(), parke.getCarType());
                }
            }
        }
        player.playNextFrame(frame);
    }

    /**
     * this method maintains the dyanmic coloring, makes sure that entities are
     * added and removed to their current edges, and from a certain threshold,
     * added to the dynamic coloring group.
     *
     * @param from
     * @param to
     * @param startPrecent
     * @param who
     * @param boundingQuery
     * @param endPrecent
     */
    private void maintainDynamicColoring(String from, String to, double startPrecent, Integer who, GroupBoundingQuery boundingQuery, double endPrecent) {
        String edge = from + " " + to;
        if (!graphData.getEdgeSet().contains(edge)) {
            String[] split = edge.split(" ");
            edge = split[1] + " " + split[0];
        }
        AZVisVertex sV = (AZVisVertex) graphData.getData(edge.split(" ")[0]);
        AZVisVertex tV = (AZVisVertex) graphData.getData(edge.split(" ")[1]);

        Edge temp = (Edge) boundingQuery.getById(edge + " dynamic");
        if (temp == null) {
            temp = new Edge(edge + " dynamic");
        }

        //what happens if start=0 and end=100 at the same time???
        if (startPrecent == 0) {
            String oldEdge = graphData.removeEdgeEntity(who.toString());
            Edge oldTemp = null;
            if (oldEdge != null) {
//                String[] split = oldEdge.split(" ");
//                oldEdge = split[0] + " " + split[1];
                oldTemp = (Edge) boundingQuery.getById(oldEdge + " dynamic");
            }
            
            if (oldTemp != null) {
                int numEdges = graphData.getEdgeEntities(oldEdge).size();
                if (numEdges <= THRESHOLD) {
                    Object removed = boundingQuery.remove("DYNAMIC_COLORED", "EDGES", sV.getX(), sV.getY(), oldTemp);
                }
            }

            graphData.addEntityToEdge(edge, who.toString());
            int numEdges = graphData.getEdgeEntities(edge).size();
            if (numEdges == THRESHOLD+1) {
                boundingQuery.addToGroup("DYNAMIC_COLORED", "EDGES", sV.getX(), sV.getY(), Math.abs(sV.getX() - tV.getX()), Math.abs(sV.getY() - tV.getY()), temp);
            }
        }

    }

    private Location translateToLocation(String src, String target, Double precentage) {
//        String src = graphData.getEdgeSource(edgeName);
//        String target = graphData.getEdgeTarget(edgeName);
        AZVisVertex sV = (AZVisVertex) graphData.getData(src);
        AZVisVertex tV = (AZVisVertex) graphData.getData(target);

        return new Location(sV.getX() + (tV.getX() - sV.getX()) * precentage / 100.0,
                sV.getY() + (tV.getY() - sV.getY()) * precentage / 100.0);
    }

    private double getAngle(String src, String target) {
        AZVisVertex sV = (AZVisVertex) graphData.getData(src);
        AZVisVertex tV = (AZVisVertex) graphData.getData(target);
        return (180 * Math.atan2(tV.getY() - sV.getY(), tV.getX() - sV.getX()) / Math.PI);
    }
}
