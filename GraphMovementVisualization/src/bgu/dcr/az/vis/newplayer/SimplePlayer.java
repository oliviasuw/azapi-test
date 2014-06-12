/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.newplayer;

import bgu.dcr.az.vis.player.api.Frame;
import bgu.dcr.az.vis.player.api.Player;
import bgu.dcr.az.vis.presets.map.drawer.DrawerInterface;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Shl
 */
public class SimplePlayer implements Player {

    private final GroupBoundingQuery query;
    private final DrawerInterface drawer;
    private FrameProcessor frameProcessor;
    private final SimpleDoubleProperty millisPerFrame;
    private final SimpleIntegerProperty fps;

    public SimplePlayer(GroupBoundingQuery query, DrawerInterface drawer, double millisPerFrame, int fps) {
        this.query = query;
        this.drawer = drawer;
        this.frameProcessor = null;
        this.millisPerFrame = new SimpleDoubleProperty(millisPerFrame);
        this.fps = new SimpleIntegerProperty(fps);
    }

    @Override
    public DoubleProperty millisPerFrameProperty() {
        return millisPerFrame;
    }

    public double getMillisPerFrame() {
        return millisPerFrame.get();
    }

    public void setMillisPerFrame(double millis) {
        millisPerFrame.set(millis);
    }

    @Override
    public IntegerProperty framesPerSecondProperty() {
        return fps;
    }

    public int getFramesPerSecond() {
        return fps.get();
    }

    public void setFramesPerSeccond(int fps) {
        this.fps.set(fps);
    }

    @Override
    public void play() {
        stop();
        frameProcessor = new FrameProcessor(this);
        frameProcessor.start();
    }

    @Override
    public void pause() {
        if (frameProcessor != null) {
            frameProcessor.pause();
        }
    }

    @Override
    public void resume() {
        if (frameProcessor != null) {
            frameProcessor.resume();
        }
    }

    @Override
    public void stop() {
        if (frameProcessor != null) {
            frameProcessor.stop();
        }
    }

    @Override
    public boolean isPaused() {
        return frameProcessor != null && frameProcessor.isPaused();
    }

    @Override
    public boolean isStopped() {
        return frameProcessor != null || frameProcessor.isStopped();
    }
    
    public void draw() {
        drawer.draw();
    }

    @Override
    public GroupBoundingQuery getQuery() {
        return query;
    }
    
    public void addFrameFinishListener(ChangeListener<Boolean> listener) {
        frameProcessor.addFrameFinishListener(listener);
    }
    
    public void playNextFrame(Frame frame) {
        frameProcessor.playNextFrame(frame);
    }
   

}
