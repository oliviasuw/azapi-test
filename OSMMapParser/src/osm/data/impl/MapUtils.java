/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import java.io.File;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import osm.data.api.Bounds;
import osm.data.api.Map;
import osm.data.api.Node;
import osm.data.api.Tag;
import osm.data.api.Way;

/**
 *
 * @author Shl
 */
public class MapUtils {

    public static Map readMap(File file) throws Exception {
        InMemoryMap map = new InMemoryMap();
        Builder parser = new Builder();
        Document doc = parser.build(file);

        Element root = doc.getRootElement();
        int numberOfChilds = root.getChildElements().size();
        int per = -1;

        for (int i = 0; i < numberOfChilds; i++) {
            Element child = root.getChildElements().get(i);

            switch (child.getLocalName()) {
                case "bounds":
                    map.setBounds(OSMNodes.Bounds.<Bounds>readData(child, map));
                    break;
                case "node":
                    map.addNode(OSMNodes.Node.<Node>readData(child, map));
                    break;
                case "way":
                    map.addWay(OSMNodes.Way.<Way>readData(child, map));
                    break;
                default:
                    break;
            }
            
            System.out.println("Processed " + (((float)i / (float)numberOfChilds) * 100) + "%");
        }

        return map;
    }

    private enum OSMNodes {

        Bounds {
            @Override
            Bounds readData(Element element, Map map) {
                double minY = Double.valueOf(element.getAttributeValue("minlat"));
                double minX = Double.valueOf(element.getAttributeValue("minlon"));
                double maxY = Double.valueOf(element.getAttributeValue("maxlat"));
                double maxX = Double.valueOf(element.getAttributeValue("maxlon"));

                return new SimpleBounds(minX, maxX, minY, maxY);
            }
        }, Node {
            @Override
            Node readData(Element element, Map map) {
                long id = Long.valueOf(element.getAttributeValue("id"));
                double y = Double.valueOf(element.getAttributeValue("lat"));
                double x = Double.valueOf(element.getAttributeValue("lon"));
                boolean visible = Boolean.valueOf(element.getAttributeValue("visible"));

                SimpleNode node = new SimpleNode(x, y, id, visible);

                for (int i = 0; i < element.getChildElements().size(); i++) {
                    Element child = element.getChildElements().get(i);

                    switch (child.getLocalName()) {
                        case "tag":
                            node.addTag(OSMNodes.Tag.<Tag>readData(child, map));
                            break;
                        default:
                            throw new RuntimeException("Unsupported tag: " + child.getLocalName());
                    }
                }

                return node;
            }
        }, Tag {
            @Override
            Tag readData(Element element, Map map) {
                String k = element.getAttributeValue("k");
                String v = element.getAttributeValue("v");

                return new SimpleTag(k, v);
            }
        }, Way {
            @Override
            Way readData(Element element, Map map) {
                long id = Long.valueOf(element.getAttributeValue("id"));
                boolean visible = Boolean.valueOf(element.getAttributeValue("visible"));
                SimpleWay way = new SimpleWay(map, id, visible);

                for (int i = 0; i < element.getChildElements().size(); i++) {
                    Element child = element.getChildElements().get(i);

                    switch (child.getLocalName()) {
                        case "nd":
                            way.addNodeId(OSMNodes.Nd.<Long>readData(child, map));
                            break;
                        case "tag":
                            way.addTag(OSMNodes.Tag.<Tag>readData(child, map));
                            break;
                        default:
                            throw new RuntimeException("Unsupported tag: " + child.getLocalName());
                    }
                }

                return way;
            }
        }, Nd {
            @Override
            Long readData(Element element, Map map) {
                Long ref = Long.valueOf(element.getAttributeValue("ref"));
                return ref;
            }
        };

        abstract <T> T readData(Element element, Map map);
    }
}
