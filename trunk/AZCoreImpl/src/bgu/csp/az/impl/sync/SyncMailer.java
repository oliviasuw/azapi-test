/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bgu.csp.az.api.MessageQueue;
import bgu.csp.az.api.SystemClock;
import bgu.csp.az.impl.AbstractMailer;

/**
 *
 * @author Inna
 */
public class SyncMailer extends AbstractMailer{
    SystemClock clock;

    public void setClock(SystemClock clock) {
        this.clock = clock;
    }

    @Override
    protected MessageQueue generateNewMessageQueue() {
       return new DoubleMessageQueue(clock);
    }

}
