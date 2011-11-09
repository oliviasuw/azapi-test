/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

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

    List<Message> retriveAll();
    
}
