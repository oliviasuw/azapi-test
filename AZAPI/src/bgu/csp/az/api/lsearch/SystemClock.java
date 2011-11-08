/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.lsearch;

/**
 *
 * @author bennyl
 */
public interface SystemClock {
    void tick() throws InterruptedException;
    long time();
    void close();
    void addTickListener(TickListener tickListener);
    void removeTickListener(TickListener tickListener);
    
    public static interface TickListener{
        void onTickHappend(SystemClock sender);
    }
}
