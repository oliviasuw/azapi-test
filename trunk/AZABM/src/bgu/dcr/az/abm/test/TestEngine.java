/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.test;

import bgu.dcr.az.abm.api.World;
import bgu.dcr.az.abm.exen.ABMExecution;
import bgu.dcr.az.abm.exen.info.TickInfo;
import bgu.dcr.az.abm.impl.AgentDataManager;
import bgu.dcr.az.abm.impl.WorldImpl;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Eran
 */
public class TestEngine {

    private static final int NUM_TICKS = 10000;

    public static void main(String[] args) throws Exception {
        
        AgentDataManager manager = new AgentDataManager();
        manager.init(null);
        
        Talker t0 = manager.createAgentData(0, Talker.class);
        Talker t1 = manager.createAgentData(1, Talker.class);
        
        System.out.println("Changes are " + manager.getDataChanges(Talker.class));
        
        t0.setShouldTalk(true);
        
        System.out.println("Changes are " + manager.getDataChanges(Talker.class));
        
        manager.tick(0);
        
        System.out.println("Changes are " + manager.getDataChanges(Talker.class));
                
        t0.setShouldTalk(true);
        t1.setShouldTalk(true);
        
        System.out.println("Changes are " + manager.getDataChanges(Talker.class));
        
        
        System.out.println("class of t1: " + t1.getClass().getName());
        System.out.println("class of t0: " + t0.getClass().getName());
        
        if (1==1) return;
        World w = createWorld();

        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        Scheduler scheduler = new MultithreadedScheduler(threadPool);

        ABMExecution ex = new ABMExecution(w, null);
        ex.informationStream().listen(TickInfo.class, t -> {
            if (t.getTickNumber() % 10000 == 0) {
                System.out.println("tick: " + t.getTickNumber());
            }
            if (t.getTickNumber() == 1000000) {
                ex.terminate();
            }
        });

        ex.execute(scheduler, 1);
        threadPool.shutdown();
    }

    private static World createWorld() {
        WorldImpl w = new WorldImpl();
        new TestWorldGenerator().generate(w, new Random());
        return w;
    }

}
