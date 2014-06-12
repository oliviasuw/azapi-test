/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.newplayer;

import bgu.dcr.az.vis.player.api.Frame;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Shl
 */
class FrameProcessor extends AnimationTimer {

    private boolean isPaused;
    private boolean isStopped;

    private int fps = 0;
    private int lastFps = 0;
    private long lastSecondStart;
    private long pauseSecondDelta;

    private long frameStartTime;
    private Frame currentFrame;

    private final DoubleBinding frameDurationInNano;
    private final SimplePlayer player;

    private final BooleanProperty finishedCurrFrame = new SimpleBooleanProperty(true);
//    private boolean finishedCurrFrame = true;

    FrameProcessor(SimplePlayer player) {
        this.player = player;
        this.isPaused = false;
        this.isStopped = true;

        frameDurationInNano = player.millisPerFrameProperty().multiply(1000000);
    }

    @Override
    public void handle(long l) {
        if (isPaused) {
            frameStartTime = l - pauseSecondDelta;
        }

        double frameProgress = Math.min(1, (l - frameStartTime) / frameDurationInNano.doubleValue());

        player.draw();

        if (currentFrame != null) {
            currentFrame.update(frameProgress, player.getQuery());
        }

        if (!isPaused && (currentFrame == null || frameProgress == 1)) {
            finishedCurrFrame.set(true);
        }

        measureFPS(l);
    }

    private void measureFPS(long l) {
        if (l - lastSecondStart > 1000000000) {
            lastFps = fps;
            fps = 0;
            lastSecondStart = l;
        }
        fps++;

        player.setFramesPerSeccond(lastFps);

    }

    /**
     * should only invoke this if current frame is finished!
     *
     * @param frame
     */
    public void playNextFrame(Frame frame) {
        finishedCurrFrame.set(false);
        if (frame != null) {
            frame.initialize(player);
            currentFrame = frame;
            frameStartTime = System.nanoTime();
        }
    }

    @Override
    public void start() {
        this.isPaused = false;
        this.isStopped = false;

        lastFps = 0;
        lastSecondStart = System.nanoTime();

        super.start();
    }

    public void pause() {
        isPaused = true;
        pauseSecondDelta = System.nanoTime() - frameStartTime;
    }

    public void resume() {
        isPaused = false;
    }

    @Override
    public void stop() {
        isStopped = true;
        super.stop();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void addFrameFinishListener(ChangeListener<Boolean> listener) {
        finishedCurrFrame.addListener(listener);
    }

}
