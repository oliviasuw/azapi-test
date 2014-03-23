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
public class ExternalMessageReceivedInfo {
    private final long messageId;
    private final int sender;
    private final int recepient;
    private final String name;

    public ExternalMessageReceivedInfo(long messageId, int sender, int recepient, String name) {
        this.messageId = messageId;
        this.sender = sender;
        this.recepient = recepient;
        this.name = name;
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
    
}
