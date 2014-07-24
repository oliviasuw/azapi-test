/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphData;

import static java.lang.System.exit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import services.AddressDispatch;
import statistics.Utility;

/**
 *
 * @author Shl
 */
public class Graph {

    private static final Random rand = Utility.rand;

    private static double carLength; //car's length on the road.
    private AddressDispatch dispatcher;

    private HashSet<String> vertices; //collection of the vertices in the graph
    private HashMap<String, HashMap<String, String>> vAttr; //maps each vertex to it's attributes
    private HashMap<String, HashSet<String>> incomeVertices; //maps each vertex to the set of vertices heading to it.
    private HashMap<String, double[]> vPos; //[x,y] coordinates of each vertex

    private HashMap<Integer, CarInfo> cars; //maps each car to it's relevant data

    private HashMap<String, HashMap<String, EdgeData>> edges; //maps each edge to it's relevant data
    private String[] edgesVertices;

    public Graph(double carLen) {
        incomeVertices = new HashMap<>();
        vertices = new HashSet<>();
        vAttr = new HashMap<>();
        vPos = new HashMap<>();

        edges = new HashMap<>();

        carLength = carLen;
        cars = new HashMap<>();
    }

    /**
     * add vertex to the graph - add it to the set of vertices, and initiate an
     * entry for that vertex in the edges map
     *
     * @param name
     * @param cord
     */
    public void addVertex(String name, double[] cord) {
//          the following line adds also poligons' vertices, which are not 
//          needed by me, and makes the simulation VERY slow.
//          (by adding vertices when adding edges, we ensure we add vertices of the road map ONLY)
        vertices.add(name);
        vPos.put(name, cord);
    }

    /**
     * add the edges (v1,v2) and (v2,v1) to the graph. the two other parameters
     * represent the edge's length, and it's maximal cars capacity.
     *
     * @param v1
     * @param v2
     * @param roadLen
     * @param maxCap
     * @param tags
     */
    public void addEdge(String v1, String v2, double roadLen, int maxCap, HashMap<String, String> tags) {
        boolean oneway = "yes".equals(tags.get("oneway"));

        vertices.add(v1);
        vertices.add(v2);
        edges.putIfAbsent(v1, new HashMap<>());
        edges.putIfAbsent(v2, new HashMap<>());

        incomeVertices.putIfAbsent(v1, new HashSet<>());
        incomeVertices.putIfAbsent(v2, new HashSet<>());

        if (!vertices.contains(v1) || !vertices.contains(v2)) {
            throw new UnsupportedOperationException(String.format("Can't create edge - either the vertex %s or %s does'nt exist!", v1, v2));
        }
        try {
            if (edges.get(v1).get(v2) == null) { //prevent edges duplicity
                edges.get(v1).put(v2, new EdgeData(roadLen, maxCap));
                edges.get(v1).get(v2).addAttributes(tags);
                incomeVertices.get(v2).add(v1);

                if (!oneway) {
                    edges.get(v2).put(v1, new EdgeData(roadLen, maxCap));
                    edges.get(v2).get(v1).addAttributes(tags);
                    incomeVertices.get(v1).add(v2);
                }
            }
        } catch (Exception e) {
            System.out.println(String.format("problem while adding edge (%s, %s)", v1, v2));
            exit(1);
        }
    }

    /**
     * return true if there's an edge between the two given vertices
     *
     * @param src
     * @param target
     * @return
     */
    public boolean hasEdge(String src, String target) {
        return this.edges.get(src).get(target) != null;
    }

    /**
     * Check whether the two vertices have the same name, or they have different
     * names, BUT they have the same coordinates in the map.
     *
     * @param v1
     * @param v2
     * @return
     */
    public boolean sameVertex(String v1, String v2) {
        return v1.equals(v2) || (hasEdge(v1, v2) && calcEdgeLength(v1, v2) == 0);
    }

    /**
     * Return a set containing all vertices in the graph which have more than
     * zero edges.
     *
     * @return
     */
    public String[] getConnectedVertexSet() {
        return edgesVertices;
    }

    /**
     * Return a set containing all vertices in the graph.
     *
     * @return
     */
    public HashSet<String> getEntireVertexSet() {
        return vertices;
    }

    /**
     * return a set containing all names of vertices who has an edge heading
     * into v.
     *
     * @param v
     * @return
     */
    public HashSet<String> getIncomingVertices(String v) {
        return this.incomeVertices.get(v);
    }

    /**
     * get adjacent vertices of 'vertex'
     *
     * @param vertex
     * @return
     */
    public Set<String> getAdjacentsOf(String vertex) {
        return edges.get(vertex).keySet();
    }

    /**
     * return w(u,v)
     *
     * @param u
     * @param v
     * @return
     */
    public double calcEdgeLength(String u, String v) {
        return edges.get(u).get(v).getRoadLength();
    }

    /**
     * Calculate the flight-distance between two vertices.
     *
     * @param u
     * @param v
     * @return
     */
    public double distance(String u, String v) {
        double[] upos = vPos.get(u), vpos = vPos.get(v);
        double dx = upos[0] - vpos[0], dy = upos[1] - vpos[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * calculate the sum of lengths of the edges in 'path'.
     *
     * @param path
     * @return
     */
    public ArrayDeque<Double> calcPathLength(ArrayDeque<String> path) {
        ArrayDeque<Double> lens = new ArrayDeque<>();
        String prev = null, curr;
        boolean flag = true;
        for (String currS : path) {
            if (flag) {
                prev = currS;
                flag = false;
            } else {
                curr = currS;
                lens.add(calcEdgeLength(prev, curr));
                prev = curr;
            }
        }
        return lens;
    }

    /**
     * return the progress of car 'id' in it's current segment
     *
     * @param id
     * @return
     */
    public double getPercantage(int id) {
        return cars.get(id).getPercantage();
    }

    /**
     * set the progress of car 'id' in it's current segment
     *
     * @param id
     * @param per
     */
    public void setPercantage(int id, double per) {
        cars.get(id).setPercantage(per);
    }

    /**
     * return true if edge (v,u) has reached it's maximal cars capacity
     *
     * @param v
     * @param u
     * @return
     */
    public boolean reachedMaxCapacity(String v, String u) {
        return edges.get(v).get(u).getMaxCapacity() == edges.get(v).get(u).getNumOfCars();
    }

    /**
     * insert car 'id' into edge (u,v), and update that car info in the
     * hash-table
     *
     * @param id
     * @param u
     * @param v
     */
    public void insertCarToEdge(int id, String u, String v) {
        if (cars.get(id) == null) {
            cars.put(id, new CarInfo(0));
        } else {
            cars.get(id).setPercantage(0);
        }
        
        if("".equals(u) || "".equals(v))
            System.out.println("H!K@#K!");
        cars.get(id).setEdge(u, v);

        edges.get(u).get(v).addCar(id);
    }

    /**
     * remove car 'id' from it's current edge, and update also the car's info in
     * the hash-table
     *
     * @param id
     */
    public void removeCarFromEdge(int id) {
        String src = cars.get(id).getSource();
        String tar = cars.get(id).getTarget();
//        System.out.println("source: {" + src + "}");
//        System.out.println("target: {" + tar + "}");
        edges.get(src).get(tar).removeCar(id); //remove car 'id' from the edge 
        cars.get(id).setEdge("", ""); //update the edge of the car 'id'
    }

    /**
     * return the position of the nearest car ahead of the car 'id'
     *
     * @param id
     * @return
     */
    public double carAhead(int id) {
        String src = cars.get(id).getSource();
        String tar = cars.get(id).getTarget();
        double minP = cars.get(id).getPercantage(), maxP = Integer.MAX_VALUE;

        for (Integer carID : edges.get(src).get(tar).getCarsID()) {
            double idPercantage = cars.get(carID).getPercantage();
            if (minP < idPercantage && idPercantage < maxP) {
                maxP = idPercantage;
            }
        }

        if (maxP == Integer.MAX_VALUE) { //no car ahead
            return 100;
        } else { //there's a car at maxP%, so car 'id' can drive until (maxP - carLength)% (considering the cars' length)
            return maxP - carLength;
        }
    }

    /**
     * returns a number N which contains the following data: N/100 =
     * #empty-edges (till the first car in the path). N%100 = position of the
     * last car in the (#empty-edges + 1)'th edge.
     *
     * @param path
     * @return
     */
    public double lastCarInPath(ArrayDeque<String> path) {
        if (path.size() < 2) {
            throw new UnsupportedOperationException("lastCarInPath:: path's size < 2");
        }

        double emptyEdges = 0, currLast = 0;
        String src = null, target;
        boolean flag = true;
        for (String v : path) {
            if (flag) {
                src = v;
                flag = false;
            } else {
                target = v;
                currLast = lastInSegment(src, target);

                if (currLast < 100) {
                    return emptyEdges * 100 + currLast;
                } else {
                    currLast = 0;
                    emptyEdges++;
                }
                src = target;
            }
        }

        return emptyEdges * 100 + currLast;
    }

    /**
     * return the position of the last car inside (src, target). returns 100 if
     * empty.
     *
     * @param src
     * @param target
     * @return
     */
    public double lastInSegment(String src, String target) {
        double minPos = Double.MAX_VALUE;
        EdgeData e = edges.get(src).get(target);
        for (Integer id : e.getCarsID()) {
            minPos = Math.min(minPos, cars.get(id).getPercantage());
        }
        return ((minPos == Double.MAX_VALUE) ? 100 : minPos);
    }

    /**
     * convert GraphData instance to Graph instance
     *
     * @param parsedGraph
     * @param carLen
     * @return
     */
    public static Graph convertGraph(GraphData parsedGraph, double carLen) {
        Graph converted = new Graph(carLen);

        //import vertices to new graph
        for (String v : parsedGraph.getVertexSet()) {
            AZVisVertex ver = (AZVisVertex) parsedGraph.getData(v);
            
            converted.addVertex(v, new double[]{ver.getX(), ver.getY()});
            converted.addVertexAttributes(v, ver.getTags());
        }

        //import edges to new graph
        for (String e : parsedGraph.getEdgeSet()) { //e = "source target"
            String[] v = e.split(" "); //v[0] = source, v[1] = target
            double edgeLen = parsedGraph.calcEdgeLength(v[0], v[1]);
            HashMap<String, String> edgeAttrMap = (HashMap<String, String>) parsedGraph.getData(v[0] + " " + v[1]);

            AZVisVertex v0 = (AZVisVertex) parsedGraph.getData(v[0]);
            AZVisVertex v1 = (AZVisVertex) parsedGraph.getData(v[1]);
            
            converted.addVertexAttributes(v[0], v0.getTags());
            converted.addVertexAttributes(v[1], v1.getTags());

            int capacity = Utility.calculateSegmentCapacity(edgeAttrMap);

            converted.addEdge(v[0], v[1], edgeLen, capacity, edgeAttrMap); // <~~~~~~~~~~~~~~ change to reasonable capacity
        }
//        converted.edgesArray = new String[converted.edges.size()];
//        converted.edges.keySet().toArray(converted.edgesArray);

        HashSet<String> edgeVertices = new HashSet<>(converted.edges.keySet());
        for (HashMap<String, EdgeData> connected : converted.edges.values()) {
            edgeVertices.addAll(connected.keySet());
        }
        converted.edgesVertices = new String[edgeVertices.size()];
        edgeVertices.toArray(converted.edgesVertices);

        return converted;
    }

    /**
     * return a random vertex in the graph
     *
     * @return
     */
    public String getRandomVertex() {
        int size = this.edgesVertices.length;
        int item = rand.nextInt(size); // In real life, the Random object should be rather more shared than this

        return edgesVertices[item].split(" ")[0];
    }

    /**
     * Checks whether (v1,v2) is the only edge heading to vertex v2.
     *
     * @param v1
     * @param v2
     * @return
     */
    public boolean isOnlyVertexHeading(String v1, String v2) {
        return incomeVertices.get(v2).contains(v1) && incomeVertices.get(v2).size() == 1;
    }

    /**
     * check if (v1,v2) is a segment in a bigger road (which is divided into
     * small segments)
     *
     * @param v
     * @return
     */
    public boolean isPartialRoad(String v) {
        Set<String> out = edges.get(v).keySet(), in = incomeVertices.get(v);
        String v1 = null, v2 = null;
        int connected = 0;
        if (out.size() > 2 || in.size() > 2) //at most connected to two other vertices
        {
            return false;
        }

        for (String ver : out) {
            connected++;
            if (v1 == null) {
                v1 = ver;
            } else {
                v2 = ver;
            }
        }

        for (String ver : in) {
            if (((v1 == null) || !sameVertex(ver, v1)) && ((v2 == null) || !sameVertex(ver, v2))) {
                if (connected++ == 2) //connected to 3+ vertices -> not part of a road.
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * generate a random home-vertex for some new worker
     *
     * @return
     */
    public String getHomeVertex() {
        return dispatcher.dispatchHomeAddress();
    }

    /**
     * generate a random work-vertex for some new worker
     *
     * @return
     */
    public String getWorkVertex() {
        return dispatcher.dispatchWorkAddress();
    }

    /**
     * return the attributes of vertex v
     *
     * @param v
     * @return
     */
    public HashMap<String, String> getVertexAttributes(String v) {
        return this.vAttr.get(v);
    }

    /**
     * add a collection of attributes to a vertex v.
     *
     * @param v
     * @param attributes
     */
    private void addVertexAttributes(String v, HashMap<String, String> attributes) {
        if (this.vAttr.get(v) == null) {
            this.vAttr.put(v, new HashMap<>());
        }

        this.vAttr.get(v).putAll(attributes);
    }

    public HashMap<String, String> getEdgeAttributes(String src, String dest) {
        return this.edges.get(src).get(dest).getAttributes();
    }

    public double getMostFarPos(String src, String target) {
        HashSet<Integer> carsID = this.edges.get(src).get(target).getCarsID();
        double mostFar = 0;

        if (carsID.isEmpty()) {
            return 0;
        }

        for (Integer id : carsID) {
            mostFar = Math.max(mostFar, this.cars.get(id).getPercantage());
        }

        return mostFar;
    }
}
