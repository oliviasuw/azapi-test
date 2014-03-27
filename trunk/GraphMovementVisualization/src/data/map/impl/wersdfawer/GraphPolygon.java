/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Shl
 */
public class GraphPolygon {

    private final Collection<String> nodes;
    private final HashMap<String, String> params;
    private Point2D.Double center = null;
    private double area;
    private double width;
    private double height;

    GraphPolygon(Collection<String> pNodes, HashMap<String, String> params) {
        this.nodes = pNodes;
        this.params = params;
    }

    public Collection<String> getNodes() {
        return nodes;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * returns the center point of the polygon. CANNOT USE THIS BEFORE YOU HAVE
     * CALLED SETCENTER(GraphData) AT LEAST ONCE BEFORE!
     *
     * @return
     */
    public Point2D.Double getCenter() {
        if (center == null) {
            System.out.println("CENTER FOR THIS POLYGOn HAS NOT BEEN SET!!!");
        }
        return center;
    }
    
    public double getArea() {
        return area;
    }

    public void setCenter(GraphData graphData) {
        if (center != null) {
//            System.out.println("cant change center!");
            return;
        }
        Point2D[] polyPoints = new Point2D[nodes.size()];
        int i=0;
        for (String nodeName : nodes) { 
            AZVisVertex vert = (AZVisVertex) graphData.getData(nodeName);
            polyPoints[i] = new Point2D.Double(vert.getX(), vert.getY());
            i++;
        }
        Point2D ans = centerOfMass(polyPoints);
        this.center = new Point2D.Double(ans.getX(), ans.getY());
        this.area = area(polyPoints);
        setWidth(polyPoints);
        setHeight(polyPoints);
    }

    private double area(Point2D[] polyPoints) {
        int i, j, n = polyPoints.length;
        double area = 0;

        for (i = 0; i < n; i++) {
            j = (i + 1) % n;
            area += polyPoints[i].getX() * polyPoints[j].getY();
            area -= polyPoints[j].getX() * polyPoints[i].getY();
        }
        area /= 2.0;
        return (area);
    }

    private Point2D centerOfMass(Point2D[] polyPoints) {
        double cx = 0, cy = 0;
        double area = area(polyPoints);
        // could change this to Point2D.Float if you want to use less memory
        Point2D res = new Point2D.Double();
        int i, j, n = polyPoints.length;

        double factor = 0;
        for (i = 0; i < n; i++) {
            j = (i + 1) % n;
            factor = (polyPoints[i].getX() * polyPoints[j].getY()
                    - polyPoints[j].getX() * polyPoints[i].getY());
            cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
            cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
        }
        area *= 6.0f;
        factor = 1 / area;
        cx *= factor;
        cy *= factor;
        res.setLocation(cx, cy);
        return res;
    }

    private void setWidth(Point2D[] polyPoints) {
        double min = Double.MAX_VALUE;
        double max = 0;
        for (Point2D polyPoint : polyPoints) {
            double x = polyPoint.getX();
            if (x < min) {
                min = x;
            }
            if (x>max) {
                max = x;
            }
        }
        this.width = max - min;
    }

    private void setHeight(Point2D[] polyPoints) {
        double min = Double.MAX_VALUE;
        double max = 0;
        for (Point2D polyPoint : polyPoints) {
            double y = polyPoint.getY();
            if (y < min) {
                min = y;
            }
            if (y>max) {
                max = y;
            }
        }    
        this.height = max - min;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
    
    
}
