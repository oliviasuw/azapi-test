/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;


/**
 *
 * @author bennyl
 */
public interface Process extends Runnable {

    /**
     * fire new event with the given name and parameters 
     * the parameters are key value pairs where key must be integer and value can be any object with no circular object graph
     * if no event pipe is set then this event will get thrown away
     * @param name
     * @param params 
     */
    void fire(String name, Object... params);

    /**
     * @return the inner event pipe or null if no such defined
     */
    EventPipe getEventPipe();

    /**
     * @return true if this process is finished 
     * TODO: transform to status enum.
     */
    boolean isFinished();

    /**
     * sets the event pipe for this process - process with no event pipe (null) throws away all its given events
     * @param epipe 
     */
    void setEventPipe(EventPipe epipe);

    /**
     * each execution should supply a way to stop
     * stopping every child process or thread that "belongs" to this process.
     * //TODO: maybe more sutable name will be "kill"
     */
    void stop();
    
}
