/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.common.events.EventListeners;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class SimpleInfoStream implements InfoStream {

    private Map<Class, EventListeners<InfoStreamListener>> listeners = new IdentityHashMap<>();

    @Override
    public <T> void listen(Class<T> dataType, InfoStreamListener<T> listener) {
        EventListeners<InfoStreamListener> list = listeners.get(dataType);
        if (list == null) {
            list = EventListeners.create(InfoStreamListener.class);
            listeners.put(dataType, list);
        }

        list.add(listener);
    }

    @Override
    public void write(Object data) {
        if (data == null) {
            throw new UnsupportedOperationException("cannot write null info");
        }
        write(data, data.getClass());
    }

    @Override
    public void write(Object data, Class... recepients) {
        for (Class c : recepients) {
            EventListeners<InfoStreamListener> d = listeners.get(c);
            if (d != null) {
                d.fire().info(data);
            }
        }
    }

    @Override
    public boolean hasListeners(Class dataType) {
        return listeners.containsKey(dataType);
    }

}
