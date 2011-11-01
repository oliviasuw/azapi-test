/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

/**
 *
 * @author bennyl
 */
public interface MessageQueue {

    void add(Message e);

    int size();

    Message take() throws InterruptedException;

    void waitForNewMessages() throws InterruptedException;
    
}
