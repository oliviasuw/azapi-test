/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapparser;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import osm.data.api.Node;
import osm.data.api.OSMObject;
import osm.data.api.Tag;

/**
 *
 * @author Shl
 */
public class GraphWriter {

    private static GraphWriter instance = null;
    private BufferedWriter writer = null;

    private GraphWriter() {

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("graph2.txt"), "utf-8"));
        } catch (IOException ex) {
            System.out.println("problem with graph writer");
        }
    }

    public static GraphWriter getInstance() {
        if (instance == null) {
            instance = new GraphWriter();
        }
        return instance;
    }

    public void writeSize(int x, int y) {
        try {
            writer.append("S " + x + " " + y + "\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeVertex(String name, double x, double y) {
        try {
            writer.append("V " + name + " [" + x + " " + y + "]\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeEdge(String name, String from, String to) {
        try {
            writer.append("E " + from + " " + to + "\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeEdge(String name, String from, String to, Tag tag) {
        try {
            writer.append("E " + from + " " + to + " [" + tag.getK() + "=" + tag.getV() + "]\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void writePolygon(Iterable<Node> nodes, Tag tag) {
        String path = "";
        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            Node node = it.next();
            path += " " + node.getID();
        }
        try {
            writer.append("P" + path + " E" + " [" + tag.getK() + "=" + tag.getV() + "]\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * creates a tag string for the graph, extracting interesting strings for
     * the tag map. interested specifies the interesting keys separated by
     * spaces
     *
     * @param interested
     * @param obj
     * @return
     */
    public String createTagString(OSMObject obj, String interested) {
        String[] splitKeys = interested.split(" ");
        String tags = "[";
        for (int i = 0; i < splitKeys.length; i++) {
            Collection<Tag> tagVals = obj.getTags(splitKeys[i]);
            if (tagVals != null) {
                String firstTagValue = tagVals.iterator().next().getV();
                firstTagValue = firstTagValue.replace(" ", "_");
                if (i != splitKeys.length - 1) {
                    tags += splitKeys[i] + "=" + firstTagValue + " ";
                } //last one
                else {
                    tags += splitKeys[i] + "=" + firstTagValue;
                }
            }
        }
        if (tags.charAt(tags.length()-1) == ' ') {
            tags = tags.substring(0, tags.length()-1);
        }
        tags += "]";
        return tags;
    }

    public void writeVertex(String name, double x, double y, String tags) {
        try {
            writer.append("V " + name + " [" + x + " " + y + "] " + tags + "\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeEdge(String name, String from, String to, String tagString) {
        try {
            writer.append("E " + from + " " + to + " " + tagString + "\n");
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
