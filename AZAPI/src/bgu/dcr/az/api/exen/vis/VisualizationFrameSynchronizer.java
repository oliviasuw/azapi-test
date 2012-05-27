/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.utils.RoadBlock;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Administrator
 */
public class VisualizationFrameSynchronizer {

    private int taking = 0;
    private RoadBlock roadBlock = null;
    private Semaphore lock = new Semaphore(1);
    private LinkedList<FrameSyncListener> frameSyncListeners = new LinkedList<FrameSyncListener>();
    private volatile boolean syncNeeded = false;
    private Execution ex;
    
    /**
     * this must be set before the start of the frame synchronizer
     * @param n 
     */
    public void setExecution(Execution ex){
        this.ex = ex;
    }

    public void beforeTakingMessage() {
        boolean locked = false;
        try {
            lock.acquire();
            locked = true;
            taking++;
            if (syncNeeded) {
                if (taking == ex.getNumberOfAgentRunners()) { // this is the last agent
                    for (FrameSyncListener f : frameSyncListeners) {
                        f.onFrameSync();
                    }
                    syncNeeded = false;
                    roadBlock.remove();
                } else {
                    lock.release();
                    locked = false;
                    roadBlock.pass();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); //reenterupting...
        } finally {
            if (locked) {
                lock.release();
            }
        }
    }

    public void afterTakingMessage() {
        try {
            lock.acquire();
            while (syncNeeded) {
                lock.release();
                roadBlock.pass();
                lock.acquire();
            }
            taking--;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); //reenterupting...
        } finally {
            lock.release();
        }
    }

    public boolean isSyncing(){
        return syncNeeded;
    }
    
    public void sync() {
        try {
            lock.acquire();
            if (!syncNeeded) { //sync can be called multiple times...
                syncNeeded = true;
                roadBlock = new RoadBlock();
                
                lock.release();
                beforeTakingMessage();
                afterTakingMessage();
                lock.acquire();
                
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); //reenterupting...
        } finally {
            lock.release();
        }
    }
    
    /**
     * you can call this method directly if you know that the execution is already sync to 
     * tell the listeners to produce new frame..
     */
    public void fireFrameSync(){
        for (FrameSyncListener f : frameSyncListeners){
            f.onFrameSync();
        }
    }
    
    public void addFrameSyncListener(FrameSyncListener lis){
        this.frameSyncListeners.add(lis);
    }

    public static interface FrameSyncListener {

        public void onFrameSync();
    }
}
