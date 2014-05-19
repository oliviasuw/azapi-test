/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.execs.statistics.info;

/**
 *
 * @author Zovadi
 */
public class MessageInfo {
    private final long messageId;
    private final int sender;
    private final int recepient;
    private final String name;
    private final OperationType type;

    public MessageInfo(long messageId, int sender, int recepient, String name, OperationType type) {
        this.messageId = messageId;
        this.sender = sender;
        this.recepient = recepient;
        this.name = name;
        this.type = type;
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

    public OperationType getType() {
        return type;
    }
    
    public static enum OperationType {
        Sent, Received
    }
}
