/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets;

import bgu.dcr.az.vis.player.api.VisualScene;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import data.graph.impl.GraphData;
import data.graph.impl.GraphReader;
import graphics.graph.GraphDrawer;
import javafx.scene.CacheHint;

/**
 *
 * @author Shl
 */
public class MapCanvasLayer extends CanvasLayer {
    private final GraphData graphData;
    private final GraphDrawer graphDrawer;

    /**
     * generates a new canvas layer map from the specified file path. the map is
     * represented by a jgrapht graph.
     *
     * @param scene
     * @param mapFilePath
     */
    public MapCanvasLayer(VisualScene scene, String mapFilePath) {
        super(scene);
        graphData = new GraphReader().readGraph(mapFilePath);
        graphDrawer = new GraphDrawer();
        graphDrawer.drawGraph(getCanvas(), graphData, 1);
        getCanvas().setCacheHint(CacheHint.SPEED);
        getCanvas().setCache(true);
    }
    
    public GraphData getGraphData() {
        return graphData;
    }
    
    @Override
    public void refresh() {
        
    }
    
    public void drawGraph() {
        graphDrawer.drawGraph(getCanvas(), graphData, getScale());
    }

}
