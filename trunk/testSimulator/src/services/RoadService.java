/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import data.events.impl.ParkingEvent;
import graphData.Graph;
import java.util.ArrayDeque;
import graphData.GraphData;
import java.util.HashMap;
import java.util.HashSet;
import services.searchAlgoritms.PathGenerator;
import services.searchAlgoritms.Dijkstra;
import services.searchAlgoritms.RandomDFS;
import statistics.Utility;
import testsimulator.TestSimulator;

/**
 *
 * @author Eran
 */
public class RoadService implements Service {

    private final Graph graph;
    private final PathGenerator pGen;
    private HashMap<String, TrafficLight> trafficLights;

    private HashMap<String, HashSet<String>> nearbyParkingLots; //associates a vertex with a collection of nearby parking lots.  
    private HashMap<String, String> taggedToPL; //maps each tagged PL to the actual PL it belongs to.
    private HashMap<String, ParkingLot<Integer>> parkingLots; //maps each actual PL ID to an PL instance.

    private HashMap<String, HashSet<String>> nearbyFuelStations; //associates a vertex with a collection of nearby fuel-stations.
    private HashMap<String, String> taggedToFS; //maps each taggeed FS to the actual FS it belongs to.

    private String[] entertainment;

    public RoadService() {
        System.out.print("Parsing graph ... ");
        GraphData parsed = GraphData.parseGraph("graph_tlv.txt");
        System.out.println("SUCCESS!");

        System.out.print("Converting GraphData to Graph ... ");
        this.graph = Graph.convertGraph(parsed, Utility.CAR_LENGTH);
        System.out.println("SUCCESS!");

        this.pGen = new RandomDFS(this.graph);
        this.trafficLights = new HashMap<>();
        this.parkingLots = new HashMap<>();
        this.taggedToPL = new HashMap<>();
        this.taggedToFS = new HashMap<>();
        this.nearbyParkingLots = new HashMap<>();
        this.nearbyFuelStations = new HashMap<>();
    }

    @Override
    public void init() {
        initPointsOfInterest();
        initTrafficLights();
        System.out.print(" ");
    }

    @Override
    public void tick() {
        // meanwhile, there nothing that should be done
    }

    private void initTrafficLights() {
        for (String vertex : this.graph.getConnectedVertexSet()) {
            if ("traffic_signals".equals(graph.getVertexAttributes(vertex).get("highway"))) {
//                if (this.graph.getIncomingVertices(vertex).isEmpty()) {
//                    System.out.println(vertex);
//                }
                trafficLights.put(vertex, new TrafficLight(Utility.calculate_TLInterval(), this.graph.getIncomingVertices(vertex)));
            }
        }
    }

    /**
     * Initialize parking-lots, shops, restaurants, etc.
     */
    private void initPointsOfInterest() {
        HashSet<String> initPL = new HashSet<>();
        HashSet<String> initFS = new HashSet<>();
        HashSet<String> entertain = new HashSet<>();
        int c = 0;
        //get initial group of parking lots.
        for (String v : this.graph.getEntireVertexSet()) {
            if (taggedAsPL(v)) {
                initPL.add(v);
//                taggedToPL.put(v, v);
                parkingLots.put(v, new ParkingLot<>(Utility.calculateLotCapacity()));
            } else if (taggedAsEntertainment(v)) {
                entertain.add(v);
            } else if (taggedAsFS(v)) {
                initFS.add(v);
                taggedToFS.put(v, v);
            }
        }

        this.entertainment = new String[entertain.size()];
        entertain.toArray(this.entertainment);

        //tag other vertices as parking-lots/fuel-stations according to the tagging radius.
        for (String v : this.graph.getConnectedVertexSet()) {
            for (String u : initPL) {
                boolean not_tagged = !taggedAsPL(v);
                boolean inside_tagging_radius = this.graph.distance(v, u) < Utility.RADIUS_TAGGING;
                if (not_tagged && inside_tagging_radius) {
                    taggedToPL.put(v, u);
                }
            }
            for (String u : initFS) {
                boolean not_tagged = !taggedAsFS(v);
                boolean inside_tagging_radius = this.graph.distance(v, u) < Utility.RADIUS_TAGGING;
                if (not_tagged && inside_tagging_radius) {
                    taggedToFS.put(v, u);
                }
            }
        }

        //attach a collection of nearby parking-lots to each vertex in the graph.
        for (String v : this.graph.getEntireVertexSet()) {
            for (String lot : taggedToPL.keySet()) {
                if (this.graph.distance(lot, v) <= Utility.RADIUS_SEARCH) {
                    nearbyParkingLots.putIfAbsent(v, new HashSet<>());
                    nearbyParkingLots.get(v).add(lot);
                }
            }
            for (String fuel : taggedToFS.keySet()) {
                if (this.graph.distance(fuel, v) <= Utility.RADIUS_SEARCH) {
                    nearbyFuelStations.putIfAbsent(v, new HashSet<>());
                    nearbyFuelStations.get(v).add(fuel);
                }
            }
        }
    }

    public boolean taggedAs(String v, String tag, String[] values) {
        String v_tagValue = this.graph.getVertexAttributes(v).get(tag);
        if (v_tagValue == null) {
            return false;
        } else {
            for (String value : values) {
                if (value.equals(v_tagValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether a vertex is tagged as a parking-lot.
     *
     * @param v
     * @return
     */
    public boolean taggedAsPL(String v) {
        String tag_pl = "amenity";
        String[] values_pl = new String[]{"parking", "parking_entrance"};

        return taggedAs(v, tag_pl, values_pl);
    }

    /**
     * Check if v is tagged as a fuel-station.
     *
     * @param v
     * @return
     */
    public boolean taggedAsFS(String v) {
        String tag_fs = "amenity";
        String[] values_fs = {"fuel"};

        return taggedAs(v, tag_fs, values_fs);
    }

    public boolean taggedAsHangout(String v) {
        String tag_hangout = "amenity";
        String[] values_hangout = {"cafe", "restaurant", "fast_food", "pub", "bycicle_rental"};

        return taggedAs(v, tag_hangout, values_hangout);
    }

    public boolean taggedAsShop(String v) {
        String tag_shops = "shop";
        String[] values_shops = {"cinema", "hairdresser", "supermarket", "clothes", "kiosk", "convenience", "bakery", "laundry"};

        return taggedAs(v, tag_shops, values_shops);
    }

    public boolean taggedAsEntertainment(String v) {
        return taggedAsShop(v) || taggedAsHangout(v);
    }

    /**
     * generate a path between the two given vertices
     *
     * @param from
     * @param to
     * @return
     */
    public ArrayDeque<String> getPath(String from, String to) {
        return pGen.generate(from, to);
    }

    public double getEdgeLength(String from, String to) {
        return this.graph.calcEdgeLength(from, to);
    }

    /**
     * return the length (#km's) of the given path
     *
     * @param path
     * @return
     */
    public double getPathLengths(ArrayDeque<String> path) {
        double acc = 0;

        for (double len : this.graph.calcPathLength(path)) {
            acc += len;
        }
        return acc;
    }

    /**
     * return true if there's a slot available in the road (v1,v2).
     *
     * @param v1
     * @param v2
     * @return
     */
    public boolean segmentNotFull(String v1, String v2) {
        return !graph.reachedMaxCapacity(v1, v2);
    }

    /**
     * insert the car named id into the road (from, to)
     *
     * @param id - car's id
     * @param from - edge's source
     * @param to - edge's target
     */
    public void enterSegment(int id, String from, String to) {
        graph.insertCarToEdge(id, from, to);
    }

    /**
     * draw out the car named id from the road segment
     *
     * @param id - car's id
     */
    public void exitSegment(int id) {
        graph.removeCarFromEdge(id);
    }

    public String[] getCurrSegment(int id) {
        return null;
    }

    /**
     * Finds the closest car ahead of the car named 'id', and return its place
     * in the segment (in percentage). If there's no car ahead, 100% is
     * returned.
     *
     * @param id - car's id
     * @return
     */
    public double getPositionOfNextCar(int id) {
        return graph.carAhead(id);
    }

    public double getPositionOfNextCar(int id, ArrayDeque<String> path) {
        return getPositionOfNextCar(id) + ((getPositionOfNextCar(id) < 100) ? 0 : graph.lastCarInPath(path));
    }

    /**
     * get the position of car 'id' in the relevant segment (returned data
     * between 0 to 100).
     *
     * @param id - car's id
     * @return
     */
    public double getPercantage(int id) {
        return this.graph.getPercantage(id);
    }

    /**
     * set the position of car 'id' in the relevant segment to be 'per'
     *
     * @param id - car's id
     * @param per - between 0 to 100.
     */
    public void setPercantage(int id, double per) {
        this.graph.setPercantage(id, per);
    }

    /**
     * return two vertices - home and work addresses. Also ensure that it's
     * possible to reach from one vertex to the other.
     *
     * @return
     */
    public String[] generateWorkerInfo() {
        String[] vs = new String[2];
        ArrayDeque<String> homeToWork, workToHome;

        do {
            vs[0] = this.graph.getRandomVertex();
            Object[] arr = this.taggedToPL.keySet().toArray();
//            vs[1] = this.graph.getRandomVertex();
            vs[1] = (String)arr[Utility.rand.nextInt(arr.length)];
            homeToWork = this.pGen.generate(vs[0], vs[1]);
            workToHome = this.pGen.generate(vs[1], vs[0]);
            if (this.graph.sameVertex(vs[0], vs[1]) || homeToWork.isEmpty() || workToHome.isEmpty()) {
                System.out.print("Trying again - ");
                if (homeToWork.isEmpty()) {
                    System.out.println("path (v1->v2) is empty!");
                } else if (workToHome.isEmpty()) {
                    System.out.println("path (v2->v1) is empty!");
                } else {
                    System.out.printf("same vertex: %s,%s\n", vs[0], vs[1]);
                }
            }
        } while (this.graph.sameVertex(vs[0], vs[1]) || homeToWork.isEmpty() || workToHome.isEmpty());
        return vs;
    }

    /**
     * Checks whether vertex src is the only vertex heading to dest.
     *
     * @param src
     * @param dest
     * @return
     */
    public boolean isOnlyIncomingRoad(String src, String dest) {
        return graph.isPartialRoad(src);
    }

    /**
     * Checks if the traffic-light on the edge (src, dest) is green in a
     * specific tick, or there's no traffic-light on that junction.
     *
     * @param src
     * @param dest
     * @param tick
     * @return
     */
    public boolean greenLight(String src, String dest, int tick) {
        if (this.trafficLights.get(dest) == null) {
            return true;
        } else {
            return src.equals(this.trafficLights.get(dest).getCurrent(tick));
        }
    }

    /**
     * Find a non-empty parking-lot nearby the destination, return null if
     * nothing was found.
     *
     * @param carID
     * @param src
     * @param dest
     * @return
     */
    public String findClosePL(int carID, String src, String dest) {
        if (this.nearbyParkingLots.get(dest) == null) {
            return null;
        }

        for (String taggedPL : this.nearbyParkingLots.get(dest)) {
            if (!accessible(src, taggedPL)) {
                continue;
            }
            String actualPL_name = this.taggedToPL.get(taggedPL);
            if (parkingLots.get(actualPL_name).notFull()) {
//                System.out.println("FOUND PL!!");
                parkingLots.get(actualPL_name).addCar(carID);
//                ParkingEvent moveEvent = new ParkingEvent(carID, actualPL_name, );
//                TestSimulator.eventWriter.writeEvent(TestSimulator.output, moveEvent);
                return taggedPL;
            }
        }
        return null;
    }

    /**
     * Find a fuel-station nearby the destination, return null if nothing was
     * found.
     *
     * @param carID
     * @param src
     * @param dest
     * @return
     */
    public String findCloseFS(int carID, String src, String dest) {
        if (this.nearbyFuelStations.get(dest) == null) {
            return null;
        }

        for (String taggedPL : this.nearbyFuelStations.get(dest)) {
            if (accessible(src, taggedPL)) {
                return taggedPL;
            }
        }
        return null;
    }

    public boolean accessible(String src, String taggedPL) {
        return !this.pGen.generate(src, taggedPL).isEmpty() && !this.pGen.generate(taggedPL, src).isEmpty();
    }

    /**
     * Removes a car from the parking-lot.
     *
     * @param carID
     * @param taggedPL
     * @param percentage
     */
    public void exitFromPL(int carID, String taggedPL, double percentage, boolean electric) {
        String actualPL_name = this.taggedToPL.get(taggedPL);
        parkingLots.get(actualPL_name).removeCar(carID);
        
//        double percentage = tankData.getCurrAmount() / tankData.getCapacity();
        ParkingEvent.CarType carType = (electric)? ParkingEvent.CarType.ELECTRIC: ParkingEvent.CarType.FUEL;
        ParkingEvent parkEvent = new ParkingEvent(carID, actualPL_name, percentage,ParkingEvent.InOut.OUT, carType);
        if(actualPL_name == null || carType == null )
            System.out.println("HERE!!");
        TestSimulator.eventWriter.writeEvent(TestSimulator.output, parkEvent);
        System.out.println("Sending ParkEvent:: Exit");
    }

    /**
     * Return the attributes of the given edge.
     *
     * @param src
     * @param dest
     * @return
     */
    public HashMap<String, String> getEdgeAttributes(String src, String dest) {
        return this.graph.getEdgeAttributes(src, dest);
    }

    public int waitingToEnterJunction(String junction) {
        HashSet<String> sources = graph.getIncomingVertices(junction);
        int count = 0;
        for (String src : sources) {
            if (this.graph.getMostFarPos(src, junction) == 100) {
                count++;
            }
        }
        return count;
    }

    public String getRandomHotSpot() {
        int indx = Utility.rand.nextInt(this.entertainment.length);
        return this.entertainment[indx];
    }

    public String getAssociatedPL(String taggedPL) {
        return this.taggedToPL.get(taggedPL);
    }
}
