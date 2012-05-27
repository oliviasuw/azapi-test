/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.async;

import bgu.dcr.az.exen.stat.*;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import bgu.dcr.az.api.exen.vis.VisualizationFrameSynchronizer;
import bgu.dcr.az.exen.stat.NCSCStatisticCollector.NCSCRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class AsyncVisualizationFrameSynchronizerHook {

    long[] frames;
    volatile long currentFrame = 0;

    public void hookIn(Execution ex, final VisualizationFrameSynchronizer vsync) {
        final Agent[] agents = ex.getAgents();
        frames = new long[agents.length];

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) { // need to see if this need to be synchronizad..
                long newFrame = (Long) msg.getMetadata().get("frame");
                frames[a.getId()] = Agt0DSL.max(newFrame, frames[a.getId()]);
                frames[a.getId()]++;
                if (currentFrame < frames[a.getId()]) {
                    currentFrame = frames[a.getId()];
                    vsync.sync();
                }
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int sender, int recepient, Message msg) {
                while (vsync.isSyncing()){
//                    System.out.println("Agent " + sender + "waiting for sync to get over");
                    vsync.beforeTakingMessage();
                    vsync.afterTakingMessage();
                }
                msg.getMetadata().put("frame", frames[sender]);
            }
        }.hookInto(ex);

    }
}
