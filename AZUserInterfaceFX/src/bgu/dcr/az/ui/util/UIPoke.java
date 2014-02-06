/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javax.swing.SwingUtilities;

/**
 *
 * @author User
 */
public class UIPoke {

    private static final Timer timer = new Timer();

    private final AtomicBoolean poked = new AtomicBoolean(false);
    private final Runnable runnable;
    private final long delay;
    private long lastPoke = Long.MIN_VALUE;
    private TimerTaskPrototype task;

    public UIPoke(Runnable runnable, long delayBetweenPokes) {
        this(runnable, delayBetweenPokes, UIType.JAVAFX);
    }

    public UIPoke(Runnable runnable, long delayBetweenPokes, UIType uit) {
        this.runnable = runnable;
        this.delay = delayBetweenPokes;
        lastPoke = System.currentTimeMillis() - delay;

        switch (uit) {
            case JAVAFX:
                task = new JavaFXTimerTask();
                break;
            case SWING:
                task = new SwingTimerTask();
                break;
            default:
                throw new AssertionError(uit.name());

        }
    }

    public void poke() {
        if (poked.compareAndSet(false, true)) {
            long pokeDelay = Math.max(0, delay - (System.currentTimeMillis() - lastPoke));
            timer.schedule(task.clone(), pokeDelay);
        }
    }

    public enum UIType {

        JAVAFX, SWING;
    }

    private abstract class TimerTaskPrototype extends TimerTask {

        @Override
        public abstract TimerTask clone();

    }

    private class JavaFXTimerTask extends TimerTaskPrototype {

        @Override
        public void run() {
            Platform.runLater(() -> {
                try {
                    runnable.run();
                } finally {
                    lastPoke = System.currentTimeMillis();
                    poked.set(false);
                }
            });
        }

        @Override
        public TimerTask clone() {
            return new JavaFXTimerTask();
        }
    }

    private class SwingTimerTask extends TimerTaskPrototype {

        @Override
        public void run() {
            SwingUtilities.invokeLater(() -> {
                try {
                    runnable.run();
                } finally {
                    lastPoke = System.currentTimeMillis();
                    poked.set(false);
                }
            });
        }

        @Override
        public TimerTask clone() {
            return new SwingTimerTask();
        }
    }
}
