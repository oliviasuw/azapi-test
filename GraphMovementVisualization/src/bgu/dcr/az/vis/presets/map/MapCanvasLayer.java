/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import bgu.dcr.az.vis.player.api.VisualScene;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.presets.map.drawer.DrawerInterface;
import data.map.impl.wersdfawer.GraphData;
import javafx.scene.CacheHint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Shl
 */
public class MapCanvasLayer extends CanvasLayer {

    private GraphData graphData;
    private DrawerInterface drawer;

//    private EdgeDrawer edgeDrawer;
//    private PolygonDrawer polyDrawer;
//    private SimplePolygonImageDrawer backGroundImageDrawer;
    /**
     * generates a new canvas layer map from the specified file path. the map is
     * represented by a jgrapht graph.
     *
     * @param scene
     * @param mapFilePath
     */
    public MapCanvasLayer(VisualScene scene, GraphData graphData, DrawerInterface drawer) {
        super(scene);
        this.graphData = graphData;
        this.drawer = drawer;

//        graphDrawer = new GraphDrawer(boundingQuery);
//        graphDrawer.drawGraph(getCanvas(), graphData, 1);
        getCanvas().setCacheHint(CacheHint.SPEED);
        getCanvas().setCache(true);
    }

    public GraphData getGraphData() {
        return graphData;
    }

    @Override
    public void refresh() {
        drawer.draw();
    }

}
