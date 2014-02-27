/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.Frame;
import bgu.dcr.az.vis.player.api.FramesStream;
import bgu.dcr.az.vis.player.api.Player;
import bgu.dcr.az.vis.player.api.VisualScene;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.LongBinding;
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

    private Animator animator;

    public SimplePlayer(VisualScene scene, long millisPerFrame, int fps) {
        this.scene = scene;
        this.millisPerFrame = new SimpleLongProperty(millisPerFrame);
        this.fps = new SimpleIntegerProperty(fps);
        this.animator = null;
    }

    @Override
    public VisualScene getScene() {
        return scene;
    }

    @Override
    public LongProperty millisPerFrameProperty() {
        return millisPerFrame;
    }

    public long getMillisPerFrame() {
        return millisPerFrame.get();
    }

    public void setMillisPerFrame(long millis) {
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
    public void play(FramesStream stream) {
        stop();
        animator = new Animator(this, stream);
        animator.start();
    }

    @Override
    public void pause() {
        if (animator != null) {
            animator.pause();
        }
    }

    @Override
    public void resume() {
        if (animator != null) {
            animator.resume();
        }
    }

    @Override
    public void stop() {
        if (animator != null) {
            animator.stop();
        }
    }

    @Override
    public boolean isPaused() {
        return animator != null && animator.isPaused();
    }
    
    @Override
    public boolean isStopped() {
        return animator != null || animator.isStopped();
    }

    private class Animator extends AnimationTimer {

        private final FramesStream stream;
        private final Player player;
        private boolean isPaused;
        private boolean isStopped;

        private int fps = 0;
        private int lastFps = 0;
        private long lastSecondStart;
        private long pauseSecondDelta;

        private long frameStartTime;
        private Frame currentFrame;

        private final LongBinding frameDurationInNano;

        public Animator(Player player, FramesStream stream) {
            this.stream = stream;
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

            scene.getLayers().forEach(layer -> layer.refresh());

            if (currentFrame != null) {
                currentFrame.update(frameProgress);
            }

            if (!isPaused && (currentFrame == null || frameProgress == 1)) {
                prepareNextFrame();
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

            CanvasLayer cl = (CanvasLayer) player.getScene().getLayer(1);

            cl.getCanvas().getGraphicsContext2D().strokeText("fps: " + lastFps, 14, 14);
        }

        private void prepareNextFrame() {
            Frame frame = stream.readFrame();

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

            prepareNextFrame();

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
    }

}
