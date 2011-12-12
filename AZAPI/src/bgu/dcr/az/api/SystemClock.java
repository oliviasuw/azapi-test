/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

/**
 *
 * @author bennyl
 */
public interface SystemClock {
    void tick() throws InterruptedException;
    long time();
    boolean isTicked();
    void close();
    boolean isClosed();
    void hookIn(Hooks.TickHook hook);
}