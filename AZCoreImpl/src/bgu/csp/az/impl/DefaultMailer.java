/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.MessageQueue;
import bgu.csp.az.api.infra.Execution;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author bennyl
 */
public class DefaultMailer implements Mailer {

    public static final String RECEPIENT_MESSAGE_METADATA = "DefaultMailer.RECEPIENT_MESSAGE_METADATA";
    private final Execution exec;
    private Map<String, MessageQueue[]> mailBoxes = new HashMap<String, MessageQueue[]>();

    public DefaultMailer(Execution exec) {
        this.exec = exec;
    }

    @Override
    public MessageQueue register(Agent agent, String groupKey) {
        return takeQueues(groupKey)[agent.getId()];
    }

    @Override
    public void unregisterAll() {
        mailBoxes.clear();
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        MessageQueue q = takeQueues(groupKey)[to];
        Message mcopy = msg.copy();
        mcopy.getMetadata().put(RECEPIENT_MESSAGE_METADATA, to);
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
        MessageQueue[] qs = takeQueues(groupKey);
        qs[id] = null;
        
        for (MessageQueue q : qs){
            if (q != null) return;
        }
        
        mailBoxes.remove(groupKey);
    }

    @Override
    public boolean isAllMailBoxesAreEmpty() {
        for (Entry<String, MessageQueue[]> e : mailBoxes.entrySet()){
            for (MessageQueue q : e.getValue()){
                if (q.size() > 0) return false;
            }
        }

        return true;
    }

    private MessageQueue[] takeQueues(String groupKey) {
        MessageQueue[] qs = mailBoxes.get(groupKey);
        if (qs == null) {
            final int numberOfVariables = exec.getGlobalProblem().getNumberOfVariables();
            qs = new MessageQueue[numberOfVariables];
            for (int i = 0; i < numberOfVariables; i++) {
                qs[i] = new MessageQueue();
            }
            mailBoxes.put(groupKey, qs);
        }

        return qs;
    }
}
