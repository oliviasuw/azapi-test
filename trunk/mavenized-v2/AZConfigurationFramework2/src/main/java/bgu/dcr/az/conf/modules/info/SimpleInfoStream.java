/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

import bgu.dcr.az.common.events.EventListeners;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class SimpleInfoStream implements InfoStream {

    private Map<Class, EventListeners<InfoStreamListener>> listeners = new IdentityHashMap<>();
    private Map<Object, List<RegistrationData>> removeKeyLookup = new HashMap<>();

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

    @Override
    public <T> void listen(Class<T> channel, Object removalKey, InfoStreamListener<T> listener) {
        if (removalKey == null) {
            throw new NullPointerException("removal key cannot be null");
        }
        if (channel == null) {
            throw new NullPointerException("channel cannot be null");
        }

        EventListeners<InfoStreamListener> list = listeners.get(channel);
        if (list == null) {
            list = EventListeners.create(InfoStreamListener.class);
            listeners.put(channel, list);
        }

        list.add(listener);
        removeKeyLookup.computeIfAbsent(removalKey, k -> new LinkedList<>())
                .add(new RegistrationData(listener, channel));
    }

    @Override
    public void removeListeners(Object removalKey) {
        List<RegistrationData> all = removeKeyLookup.remove(removalKey);
        if (all != null) {
            all.forEach(e -> {
                EventListeners<InfoStreamListener> v = listeners.get(e.channel);
                v.remove(e.listener);
                if (v.countListeners() == 0) {
                    listeners.remove(e.channel);
                }
            });
        }
    }

    private static class RegistrationData {

        InfoStream.InfoStreamListener listener;
        Class channel;

        public RegistrationData(InfoStreamListener listener, Class channel) {
            this.listener = listener;
            this.channel = channel;
        }

    }

}
