/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tmr;

/**
 *
 * @author bennyl
 */
public interface Timer {
    boolean haveTimeLeft();
    void start();
}
