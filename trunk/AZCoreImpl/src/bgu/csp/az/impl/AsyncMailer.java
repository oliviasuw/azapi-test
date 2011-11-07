/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.infra.Execution;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class AsyncMailer implements Mailer {

    public static final String RECEPIENT_MESSAGE_METADATA = "DefaultMailer.RECEPIENT_MESSAGE_METADATA";
    private Execution exec;
    private Map<String, DefaultMessageQueue[]> mailBoxes = new HashMap<String, DefaultMessageQueue[]>();
    Semaphore mailBoxModifierKey = new Semaphore(1);

    @Override
    public DefaultMessageQueue register(Agent agent, String groupKey) {
        return takeQueues(groupKey)[agent.getId()];
    }

    @Override
    public void setExecution(Execution exec) {
        this.exec = exec;
    }
    
    @Override
    public void unregisterAll() {
        mailBoxes.clear();
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        DefaultMessageQueue q = takeQueues(groupKey)[to];
        Message mcopy = msg.copy();
        mcopy.getMetadata().put(RECEPIENT_MESSAGE_METADATA, to);

        //System.out.println("Mailer got message to send to agent " + to + " in group " + groupKey);
        q.add(mcopy);
    }

    @Override
    public void broadcast(Message msg, String groupKey) {
        int sender = msg.getSender();
        for (int i = 0; i < exec.getGlobalProblem().getNumberOfVariables(); i++) {
            if (i != sender) {
                send(msg, i, groupKey);
            }
        }
    }

    @Override
    public void unRegister(int id, String groupKey) {
        DefaultMessageQueue[] qs = takeQueues(groupKey);
        qs[id] = null;

        for (DefaultMessageQueue q : qs) {
            if (q != null) {
                return;
            }
        }

        mailBoxes.remove(groupKey);
    }

    @Override
    public boolean isAllMailBoxesAreEmpty(String groupKey) {
        DefaultMessageQueue[] qs = takeQueues(groupKey);
        for (DefaultMessageQueue q : qs) {
            if (q.size() > 0) {
                return false;
            }
        }

        return true;
    }

    private DefaultMessageQueue[] takeQueues(String groupKey) {
        try {
            DefaultMessageQueue[] qs = mailBoxes.get(groupKey);
            if (qs == null) {
                mailBoxModifierKey.acquire();
                if (!mailBoxes.containsKey(groupKey)) { //maybe someone already modified it..
                    final int numberOfVariables = exec.getGlobalProblem().getNumberOfVariables();
                    qs = new DefaultMessageQueue[numberOfVariables];
                    for (int i = 0; i < numberOfVariables; i++) {
                        qs[i] = new DefaultMessageQueue();
                    }
                    mailBoxes.put(groupKey, qs);
                } else {
                    qs = mailBoxes.get(groupKey);
                }
                mailBoxModifierKey.release();
            }

            return qs;
        } catch (InterruptedException ex) {
            Agt0DSL.throwUncheked(ex);
            return null;
        }
    }

    public Map<String, DefaultMessageQueue[]> getMailBoxes() {
        return mailBoxes;
    }
}
