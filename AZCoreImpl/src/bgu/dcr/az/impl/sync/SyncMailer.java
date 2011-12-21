/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.sync;

import bgu.dcr.az.api.MessageQueue;
import bgu.dcr.az.api.SystemClock;
import bgu.dcr.az.impl.AbstractMailer;

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
    protected MessageQueue generateNewMessageQueue(String groupId) {
       return new DoubleMessageQueue(clock);
    }

}
