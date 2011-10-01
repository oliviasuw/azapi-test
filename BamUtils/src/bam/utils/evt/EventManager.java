/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.evt;

import bam.utils.JavaUtils.Fn1;
import bam.utils.ds.PrefixMap;
import com.google.gson.JsonElement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public enum EventManager {

    INSTANCE;
    PrefixMap<List<EventListener>> prefixMap;
    HashSet<EventListener> all;

    private EventManager() {
        prefixMap = new PrefixMap<List<EventListener>>();
        all = new HashSet<EventListener>();
    }

    public void register(String prefix, EventListener listener) {
        List<EventListener> pfl = prefixMap.getExact(prefix);
        if (pfl == null) {
            pfl = new LinkedList<EventListener>();
            prefixMap.put(prefix, pfl);
        }

        pfl.add(listener);
        all.add(listener);
    }

    public void unRegister(final EventListener listener) {
        prefixMap.map(new Fn1<List<EventListener>, List<EventListener>>() {

            @Override
            public List<EventListener> invoke(List<EventListener> arg) {
                arg.remove(listener);
                return arg;
            }
        });
    }

    public void fire(JsonElement e) {
        List<List<EventListener>> lis = prefixMap.get(e.getAsJsonObject().get("event").getAsString());
        for (List<EventListener> l : lis) {
            for (EventListener el : l) {
                el.onEvent(e);
            }
        }
    }
}