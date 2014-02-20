/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import graphmovementvisualization.AZVisVertex;
import data.graph.impl.GraphData;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class GraphDrawer {

    public void drawGraph(Canvas canvas, GraphData graphData, double scale) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();

        gc.setFill(new Color(0, 0, 0, 1));
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.strokeText("tx: " + tx + ", ty: " + ty, 14, canvas.getHeight() - 14);
        for (String vertexName : graphData.getVertexSet()) {
            AZVisVertex vertex = (AZVisVertex) graphData.getData(vertexName);
            gc.strokeRect((vertex.getX() - tx) * scale, (vertex.getY() - ty) * scale, 5 * scale, 5 * scale);

        }
        for (String edgeName : graphData.getEdgeSet()) {
            AZVisVertex source = (AZVisVertex) graphData.getData(graphData.getEdgeSource(edgeName));
            AZVisVertex target = (AZVisVertex) graphData.getData(graphData.getEdgeTarget(edgeName));
            gc.strokeLine((source.getX() - tx) * scale, (source.getY() - ty) * scale, (target.getX() - tx) * scale, (target.getY() - ty) * scale);
        }
    }

}
