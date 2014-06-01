/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.execs.lowlevel.AbstractProc;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.conf.modules.info.SimpleInfoStream;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public class InfoStreamWrapperProc extends AbstractProc implements InfoStream {

    InfoStream parent, inner;
    private ConcurrentLinkedQueue dataQueue = new ConcurrentLinkedQueue();

    public InfoStreamWrapperProc(InfoStream parent, int pid) {
        super(pid, true);
        this.parent = parent;
        this.inner = new SimpleInfoStream();
    }

    @Override
    public boolean hasListeners(Class dataType) {
        return inner.hasListeners(dataType) || parent.hasListeners(dataType);
    }

    @Override
    public void write(Object data) {
        dataQueue.add(data);
        wakeup(pid());
    }

    @Override
    public void write(Object data, Class... recepients) {
        dataQueue.add(new DataWithRecepients(recepients, data));
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

            if (data.getClass() == DataWithRecepients.class) {
                DataWithRecepients dwr = (DataWithRecepients) data;
                notifyData(dwr.data, dwr.recepients);
            } else {
                notifyData(data, data.getClass());
            }
        }

        sleep();
    }

    private void notifyData(Object data, Class... c) {
        inner.write(data, c);
        parent.write(data, c);
    }

    @Override
    protected void onIdleDetected() {
        if (dataQueue.isEmpty()) {
            sleep();
        }
    }

    @Override
    public <T> void listen(Class<T> channel, Object removalKey, InfoStreamListener<T> listener) {
        inner.listen(channel, removalKey, listener);
    }

    @Override
    public void removeListeners(Object removalKey) {
        inner.removeListeners(removalKey);
    }

    private class DataWithRecepients {

        Class[] recepients;
        Object data;

        public DataWithRecepients(Class[] recepients, Object data) {
            this.recepients = recepients;
            this.data = data;
        }

    }

}
