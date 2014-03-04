/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl.stat;

import bgu.dcr.az.execs.AbstractProc;
import bgu.dcr.az.mas.stat.InfoStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public class StatisticalInfoStreamProc extends AbstractProc implements InfoStream {

    private Map<Class, List<InfoStreamListener>> listeners = new IdentityHashMap<>();
    private ConcurrentLinkedQueue dataQueue = new ConcurrentLinkedQueue();

    public StatisticalInfoStreamProc(int pid) {
        super(pid, true);
    }

    @Override
    public <T> void listen(Class<T> dataType, InfoStreamListener<T> listener) {
        List<InfoStreamListener> list = listeners.get(dataType);
        if (list == null) {
            list = new LinkedList<>();
            listeners.put(dataType, list);
        }

        list.add(listener);
    }

    @Override
    public boolean hasListeners(Class dataType) {
        return listeners.containsKey(dataType);
    }

    @Override
    public void write(Object data) {
        dataQueue.add(data);
        wakeup(pid());
    }

    public void writeNow(Object data) {
        dataQueue.add(data);
        quota();
    }

    @Override
    protected void start() {
        // dont care.. 
    }

    @Override
    protected void quota() {
        while (!dataQueue.isEmpty()) {
            Object data = dataQueue.poll();

            for (InfoStreamListener listener : listeners.getOrDefault(data.getClass(), Collections.<InfoStreamListener>emptyList())) {
                listener.info(data);
            }
        }

        sleep();
    }

    @Override
    protected void onIdleDetected() {
        if (dataQueue.isEmpty()) {
            sleep();
        }
    }

}
