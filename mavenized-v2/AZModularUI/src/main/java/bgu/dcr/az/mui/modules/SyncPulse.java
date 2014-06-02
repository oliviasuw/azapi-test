/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.modules;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.mui.RootController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * while an experiment is running this module will write {@link Sync} event into
 * the infostream a given times in a second so that "real-time" screans can sync
 * their view, when the experiment is over, a sync event will be sent with the
 * "last" flag = true.
 *
 * @author bennyl
 */
public class SyncPulse implements Module<RootController> {

    private int sps;
    private Timeline timer;
    private Sync s;

    public SyncPulse(int sps) {
        this.sps = sps;
    }

    public int getSyncsPerSecond() {
        return sps;
    }

    public void stop() {
        s.last = true;
    }

    @Override
    public void installInto(RootController mc) {
        s = new Sync();
        InfoStream is = mc.require(InfoStream.class);

        timer = new Timeline(new KeyFrame(Duration.millis(1000.0 / sps), a -> {
            is.write(s, Sync.class);
            if (s.last) {
                timer.stop();
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public static class Sync {

        boolean last = false;

        public boolean isLast() {
            return last;
        }

    }
}
