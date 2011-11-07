/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.MessageQueue;
import bgu.csp.az.api.exp.UnRegisteredAgentException;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.lsearch.Messages;
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.impl.async.AsyncMailer;
import bgu.csp.az.impl.DefaultMessageQueue;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class SyncMailer implements Mailer, SystemClock.TickListener {

    private AsyncMailer mainMailer;
    private AsyncMailer nextStepMailer;
    private SystemClock clock;
    private ReentrantReadWriteLock lock;

    public SyncMailer() {
        this.mainMailer = new AsyncMailer();
        this.nextStepMailer = new AsyncMailer();
        lock = new ReentrantReadWriteLock();
    }

    public void setClock(SystemClock clock) {
        this.clock = clock;
        this.clock.addTickListener(this);
    }

    @Override
    public MessageQueue register(Agent agent, String groupKey) {
        nextStepMailer.register(agent, groupKey);
        return mainMailer.register(agent, groupKey);
    }

    @Override
    public void unregisterAll() {
        nextStepMailer.unregisterAll();
        mainMailer.unregisterAll();
    }

    @Override
    public void send(Message msg, int to, String groupKey) throws UnRegisteredAgentException {
        lock.readLock().lock();
        try {
            nextStepMailer.send(msg, to, groupKey);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void broadcast(Message msg, String groupKey) {
        lock.readLock().lock();
        try {
            nextStepMailer.broadcast(msg, groupKey);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void unRegister(int id, String groupKey) {
        mainMailer.unRegister(id, groupKey);
        nextStepMailer.unRegister(id, groupKey);
    }

    @Override
    public boolean isAllMailBoxesAreEmpty(String groupKey) {
        return mainMailer.isAllMailBoxesAreEmpty(groupKey) && nextStepMailer.isAllMailBoxesAreEmpty(groupKey);
    }

    @Override
    public void onTickHappend(SystemClock sender) {
        lock.writeLock().lock();
        try {
            for (Entry<String, DefaultMessageQueue[]> e : nextStepMailer.getMailBoxes().entrySet()) {
                for (int i = 0; i < e.getValue().length; i++) {
                    final DefaultMessageQueue q = e.getValue()[i];

                    //SEND ALL THE MESSAGES
                    while (q.isNotEmpty()) {
                        mainMailer.send(q.take(), i, e.getKey());
                    }
                }
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Logger.getLogger(SyncMailer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void setExecution(Execution exec) {
        nextStepMailer.setExecution(exec);
        mainMailer.setExecution(exec);
    }
}
