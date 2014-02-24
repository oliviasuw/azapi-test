/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Frame;
import bgu.dcr.az.vis.proc.api.Player;
import bgu.dcr.az.vis.proc.api.VisualScene;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 *
 * @author Shl
 */
public class SimplePlayer implements Player {

    private final VisualScene scene;

    private final LongProperty millisPerFrame;
    private final IntegerProperty fps;

    public SimplePlayer(VisualScene scene, long millisPerFrame, int fps) {
        this.scene = scene;
        this.millisPerFrame = new SimpleLongProperty(millisPerFrame);
        this.fps = new SimpleIntegerProperty(fps);
    }

    @Override
    public VisualScene getScene() {
        return scene;
    }

    @Override
    public LongProperty millisPerFrameProperty() {
        return millisPerFrame;
    }

    @Override
    public long getMillisPerFrame() {
        return millisPerFrame.get();
    }

    @Override
    public void setMillisPerFrame(long millis) {
        millisPerFrame.set(millis);
    }

    @Override
    public IntegerProperty framesPerSecondProperty() {
        return fps;
    }

    @Override
    public int getFramesPerSecond() {
        return fps.get();
    }

    @Override
    public void setFramesPerSeccond(int fps) {
        this.fps.set(fps);
    }

    @Override
    public AnimationTimer play(Frame frame) {
        frame.initialize(this);

        AnimationTimer timeline = new AnimationTimer() {
            long startTime = System.nanoTime();
            long nanoDuration = getMillisPerFrame() * 1000000;

            @Override
            public void handle(long l) {
                scene.getLayers().forEach(layer -> layer.refresh());
                
                frame.update();

                if (l - startTime > nanoDuration) {
                    stop();
                }
            }
        };
        
        timeline.start();
        
        return timeline;
    }
}
