/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

import javafx.scene.canvas.Canvas;

/**
 *
 * @author Zovadi
 */
public interface VisualizationProcessor {

    Canvas getCanvas();
    
    void play(Frame frame);
}
