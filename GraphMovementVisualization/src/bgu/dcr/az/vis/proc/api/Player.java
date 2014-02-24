/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;

/**
 *
 * @author Zovadi
 */
public interface Player {

    LongProperty millisPerFrameProperty();
    
    long getMillisPerFrame();
    
    void setMillisPerFrame(long millis);
    
    IntegerProperty framesPerSecondProperty();
    
    int getFramesPerSecond();
    
    void setFramesPerSeccond(int fps);
    
    VisualScene getScene();

    AnimationTimer play(Frame frame);
}
