/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.comtimingtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author User
 */
public class TimingUtils {

    /**
     * schedule a repeating task, return the timer object responsible for this
     * task.
     *
     * @param r
     * @param interval
     * @return
     */
    public static Timer scheduleRepeating(Runnable r, long interval) {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, interval);
        return t;
    }
}
