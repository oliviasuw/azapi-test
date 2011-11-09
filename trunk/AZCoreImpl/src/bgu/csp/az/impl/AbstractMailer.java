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
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.impl.DefaultMessageQueue;
import bgu.csp.az.impl.async.AsyncMailer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Inna
 */
public abstract class AbstractMailer implements Mailer {

    public static final String RECEPIENT_MESSAGE_METADATA = "AbstractMailer.RECEPIENT_MESSAGE_METADATA";
    private Execution exec;
    private Map<String, MessageQueue[]> mailBoxes = new HashMap<String, MessageQueue[]>();
    Semaphore mailBoxModifierKey = new Semaphore(1);

    private MessageQueue[] takeQueues(String groupKey) {
        try {
            MessageQueue[] qs = mailBoxes.get(groupKey);
            if (qs == null) {
                mailBoxModifierKey.acquire();
                if (!mailBoxes.containsKey(groupKey)) { //maybe someone already modified it..
                    final int numberOfVariables = exec.getGlobalProblem().getNumberOfVariables();
                    qs = new MessageQueue[numberOfVariables];
                    for (int i = 0; i < numberOfVariables; i++) {
                        qs[i] = generateNewMessageQueue();
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

    public AbstractMailer() {
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

    protected abstract MessageQueue generateNewMessageQueue();

    public Map<String, MessageQueue[]> getMailBoxes() {
        return mailBoxes;
    }

    @Override
    public boolean isAllMailBoxesAreEmpty(String groupKey) {
        MessageQueue[] qs = takeQueues(groupKey);
        for (MessageQueue q : qs) {
            if (q.size() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MessageQueue register(Agent agent, String groupKey) {
        return takeQueues(groupKey)[agent.getId()];
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        MessageQueue q = takeQueues(groupKey)[to];
        Message mcopy = msg.copy();
        mcopy.getMetadata().put(AsyncMailer.RECEPIENT_MESSAGE_METADATA, to);
        //System.out.println("Mailer got message to send to agent " + to + " in group " + groupKey);
        q.add(mcopy);
    }

    @Override
    public void setExecution(Execution exec) {
        this.exec = exec;
    }

    @Override
    public void unRegister(int id, String groupKey) {
        MessageQueue[] qs = takeQueues(groupKey);
        qs[id] = null;
        for (MessageQueue q : qs) {
            if (q != null) {
                return;
            }
        }
        mailBoxes.remove(groupKey);
    }

    @Override
    public void unregisterAll() {
        mailBoxes.clear();
    }
}
