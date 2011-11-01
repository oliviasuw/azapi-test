/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.MessageQueue;
import bgu.csp.az.api.exp.UnRegisteredAgentException;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.lsearch.SystemClock;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
public class LocalSearchMailer implements Mailer, SystemClock.TickListener {

    private DefaultMailer mainMailer;
    private DefaultMailer nextStepMailer;
    private SystemClock clock;

    public LocalSearchMailer(SystemClock clock, Execution ex) {
        this.mainMailer = new DefaultMailer(ex);
        this.nextStepMailer = new DefaultMailer(ex);
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
        nextStepMailer.send(msg, to, groupKey);
    }

    @Override
    public void broadcast(Message msg, String groupKey) {
        nextStepMailer.broadcast(msg, groupKey);
    }

    @Override
    public void unRegister(int id, String groupKey) {
        mainMailer.unRegister(id, groupKey);
        nextStepMailer.unRegister(id, groupKey);
    }

    @Override
    public boolean isAllMailBoxesAreEmpty() {
        return mainMailer.isAllMailBoxesAreEmpty() && nextStepMailer.isAllMailBoxesAreEmpty();
    }

    @Override
    public void onTickHappend(SystemClock sender) {
        try {
            for (Entry<String, DefaultMessageQueue[]> e : nextStepMailer.getMailBoxes().entrySet()) {
                for (int i = 0; i < e.getValue().length; i++) {
                    final DefaultMessageQueue q = e.getValue()[i];
                    while (q.size() > 0) {
                        mainMailer.send(q.take(), i, e.getKey());
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Agt0DSL.throwUncheked(ex);
        }
    }
}
