/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.tmr;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.tmr.Timer;

/**
 *
 * @author bennyl
 */
@Register(name="timer")
public class DefaultTimer implements Timer{

    @Variable(name="seconds", defaultValue="120", description="number of seconds before terminating the execution")
    long seconds;
    long endMilis = -1;
    
    public DefaultTimer() {
    }
    
    @Override
    public boolean haveTimeLeft() {
        return System.currentTimeMillis() < endMilis;
    }

    @Override
    public void start() {
        endMilis = System.currentTimeMillis() + seconds * 1000;
    }
    
}
