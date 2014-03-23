/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import com.bbn.openmap.util.quadtree.QuadTree;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class GraphDrawer {

    EdgeDrawer edgeDrawer;
    SimplePolygonImageDrawer backGroundImageDrawer;

    public GraphDrawer() {
        this.edgeDrawer = new EdgeDrawer();
        this.backGroundImageDrawer = new SimplePolygonImageDrawer();
    }

    public void drawGraph(Canvas canvas, GraphData graphData, double scale) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        gc.setFill(new Color(0, 0, 0, 1));
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.strokeText("tx: " + tx + ", ty: " + ty, 14, canvas.getHeight() - 14);
        gc.strokeText("scale: " + scale + " meter/pixel", 14, canvas.getHeight() - 25);

//        for (String vertexName : graphData.getVertexSet()) {
//            AZVisVertex vertex = (AZVisVertex) graphData.getData(vertexName);
//            gc.strokeRect((vertex.getX() - tx) * scale, (vertex.getY() - ty) * scale, 5 * scale, 5 * scale);
//        }
//        gc.beginPath();
//        
//        for (String edgeName : graphData.getEdgeSet()) {
//            AZVisVertex source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
//            AZVisVertex target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
//            HashMap<String, String> edgeData = (HashMap<String, String>) graphData.getData(edgeName);
//            
////            edgeDrawer.draw(canvas, graphData, edgeName, scale);
//            gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
//            gc.lineTo((target.getX() - tx) * scale, (target.getY() - ty) * scale);
//                        
//            
////            gc.setStroke(null);
////            gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);
//        }
//        gc.stroke();
        for (String edgeType : graphData.getTagToEdge().keySet()) {
            edgeDrawer.draw(canvas, graphData, graphData.getTagToEdge().get(edgeType), scale);
        }
        
        for (GraphPolygon polygon : graphData.getPolygons()) {
//            if (polygon.getCenter() == null || canvas.getTranslateX() > polygon.getCenter().x && canvas.getTranslateY() < polygon.getCenter().y
//                    && polygon.getCenter().x < canvas.getTranslateX() + canvas.getHeight() && polygon.getCenter().y < canvas.getTranslateY() + canvas.getHeight()) {
                edgeDrawer.draw(canvas, graphData, polygon, scale);
                backGroundImageDrawer.draw(canvas, graphData, polygon, scale);
//            }
        }

    }

}
