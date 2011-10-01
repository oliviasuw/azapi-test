/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.ds;

import bgu.csp.az.api.Agent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author bennyl
 */
public class MailRegistery {

    private HashMap<Integer, Agent> registery;
    private ReentrantReadWriteLock rwl;

    public MailRegistery() {
        registery = new HashMap<Integer, Agent>();
        rwl = new ReentrantReadWriteLock();
    }

    public void register(Agent a) {
        startWrite();
        registery.put(a.getId(), a);
        doneWrite();
    }

    public void unRegister(Integer id) {
        startWrite();
        registery.remove(id);
        doneWrite();
    }

    public Agent get(Integer id) {
        startRead();
        Agent ret = registery.get(id);
        doneRead();
        return ret;
    }

    public List<Agent> all() {
        startRead();
        LinkedList<Agent> ll = new LinkedList<Agent>(registery.values());
        doneRead();
        return ll;
    }

    public void clear() {
        startWrite();
        registery.clear();
        doneWrite();
    }

    public void startRead(){
        rwl.readLock().lock();
    }
    
    public void doneRead(){
        rwl.readLock().unlock();
    }
    
    public void startWrite(){
        rwl.writeLock().lock();
    }
    
    public void doneWrite(){
        rwl.writeLock().unlock();
    }
}
