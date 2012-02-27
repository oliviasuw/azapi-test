/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

import java.util.List;

/**
 *
 * @author bennyl
 */
public interface MessageQueue {

    void add(Message e);

    int size();

    Message take() throws InterruptedException;

    void waitForNewMessages() throws InterruptedException;

    boolean isEmpty();

    boolean isNotEmpty();

    /**
     * will cause the agent that is waiting for new messages to awake and take the message 'null'
     */
    void releaseBlockedAgent();
}
