/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Mailer;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.MessageQueue;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.impl.async.AsyncMailer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Inna
 */
public abstract class AbstractMailer implements Mailer {

    private final static boolean DEBUG = true;
    public static final String RECEPIENT_MESSAGE_METADATA = "AbstractMailer.RECEPIENT_MESSAGE_METADATA";
    private Execution exec;
    private Map<String, MessageQueue[]> mailBoxes = new HashMap<String, MessageQueue[]>();
    Semaphore mailBoxModifierKey = new Semaphore(1);

    @Override
    public void releaseAllBlockingAgents(String mailGroup) {
        for (MessageQueue q : takeQueues(mailGroup)){
            q.releaseBlockedAgent();
        }
    }

    protected MessageQueue[] takeQueues(String groupKey) {
        try {
            MessageQueue[] qs = mailBoxes.get(groupKey);
            if (qs == null) {
                mailBoxModifierKey.acquire();
                if (!mailBoxes.containsKey(groupKey)) { //maybe someone already modified it..
                    final int numberOfVariables = exec.getGlobalProblem().getNumberOfVariables();
                    qs = new MessageQueue[numberOfVariables];
                    for (int i = 0; i < numberOfVariables; i++) {
                        qs[i] = generateNewMessageQueue(groupKey);
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

    private void dp(Object what){
        if (DEBUG){
            System.out.println("" + what);
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

    protected abstract MessageQueue generateNewMessageQueue(String forGroup);

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
        //dp("Mailer got message " + msg + " to send to agent " + to + " in group " + groupKey);
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
