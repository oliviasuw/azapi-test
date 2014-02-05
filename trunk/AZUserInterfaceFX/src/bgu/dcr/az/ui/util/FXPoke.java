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

/**
 *
 * @author User
 */
public class FXPoke {

    private static final Timer timer = new Timer();

    private final AtomicBoolean poked = new AtomicBoolean(false);
    private final Runnable runnable;
    private final long delay;
    private long lastPoke = Long.MIN_VALUE;

    public FXPoke(Runnable runnable, long delayBetweenPokes) {
        this.runnable = runnable;
        this.delay = delayBetweenPokes;
        lastPoke = System.currentTimeMillis() - delay;
    }

    public void poke() {
        if (poked.compareAndSet(false, true)) {
            long pokeDelay = Math.max(0, delay - (System.currentTimeMillis() - lastPoke));
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                runnable.run();
                            } finally {
                                lastPoke = System.currentTimeMillis();
                                poked.set(false);
                            }
                        }
                    });
                }
            }, pokeDelay);
        }
    }
}
