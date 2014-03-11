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
public class MessageReceivedInfo {
    private final int sender;
    private final int recepient;
    private final String name;

    public MessageReceivedInfo(int sender, int recepient, String name) {
        this.sender = sender;
        this.recepient = recepient;
        this.name = name;
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
