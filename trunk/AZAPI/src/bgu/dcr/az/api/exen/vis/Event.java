/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Event<T> {
    
    private T userData = null;
    private MessageTransferData mdt;

    public Event() {
    }

    public Event(T userData) {
        this.userData = userData;
    }

    public T getUserData() {
        return userData;
    }

    public void setUserData(T userData) {
        this.userData = userData;
    }
    
    public MessageTransferData getMessageTransferData() {
        return mdt;
    }
    
    public void setMessageTransferData(MessageTransferData data){
        mdt = data;
    }
    
    public boolean hasMessageTransferData(){
        return mdt != null;
    }
    
    public boolean hasUserData(){
        return userData != null;
    }
    
    public static class MessageTransferData{
        public final String msgName;
        public final int from;
        public final int to;
        public final long messageTransferStartFrame;
        public final long messageTransferEndFrame;
        public final long messageId;

        public MessageTransferData(String msgName, int from, int to, long messageTransferStartFrame, long messageTransferEndFrame, long messageId) {
            this.msgName = msgName;
            this.from = from;
            this.to = to;
            this.messageTransferStartFrame = messageTransferStartFrame;
            this.messageTransferEndFrame = messageTransferEndFrame;
            this.messageId = messageId;
        }
        
    }
}
