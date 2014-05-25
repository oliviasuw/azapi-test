/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.api.Player;
import bgu.dcr.az.vis.player.impl.CanvasLayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Shl
 */
public class FPSDrawer extends GroupDrawer {
    private Player player;

    public FPSDrawer(DrawerInterface drawer, Player player) {
        super(drawer);
        this.player = player;
    }

    @Override
    public void _draw(String group, String subgroup) {
        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData(group, CanvasLayer.class);
        Canvas canvas = canvasLayer.getCanvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeText("fps: " + player.framesPerSecondProperty().get(), 14, 14);
    
    }

}
