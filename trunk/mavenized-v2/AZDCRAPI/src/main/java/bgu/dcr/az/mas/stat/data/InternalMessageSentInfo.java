/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.mas.stat.data;

/**
 *
 * @author Zovadi
 */
public class InternalMessageSentInfo {
    private final long messageId;
    private final int sender;
    private final int recepient;
    private final String name;
    private final long ccs;

    public InternalMessageSentInfo(long messageId, int sender, int recepient, String name, long ccs) {
        this.messageId = messageId;
        this.sender = sender;
        this.recepient = recepient;
        this.name = name;
        this.ccs = ccs;
    }

    public long getMessageId() {
        return messageId;
    }
    
    public int getSender() {
        return sender;
    }

    public int getRecepient() {
        return recepient;
    }

    public String getMessageName() {
        return name;
    }

    public long getConstraintChecks() {
        return ccs;
    }
    
}
