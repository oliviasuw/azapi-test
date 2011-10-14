/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.units;

import bc.dsl.JavaDSL.Fn1;
import bc.swing.pfrm.events.Event;
import bc.swing.pfrm.events.EventListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public enum EventBusUnit {
    UNIT;
    
    Map<String, List<WeakReference<EventListener>>> listeners = new HashMap<String, List<WeakReference<EventListener>>>();
    
    private List<WeakReference<EventListener>> getListenerList(String event){
        List<WeakReference<EventListener>> x = listeners.get(event);
        if (x == null){
            x = new LinkedList<WeakReference<EventListener>>();
            listeners.put(event, x);
        }
        
        return x;
    }
    
    private void iterateListeners(String event, Fn1<Boolean, EventListener> mtd){
        List<WeakReference<EventListener>> elist = getListenerList(event);
        Iterator<WeakReference<EventListener>> iter = elist.iterator();
        
        while (iter.hasNext()){
            WeakReference<EventListener> w = iter.next();
            EventListener l = w.get();
            if (l == null){
                iter.remove();
            }else {
                boolean keep = mtd.invoke(l);
                if (!keep) iter.remove();
            }
        }
    }
    
    public void fire(final Event e){
        iterateListeners(e.getName(), new Fn1<Boolean, EventListener>() {

            @Override
            public Boolean invoke(EventListener arg) {
                arg.onEvent(e);
                return true;
            }
        });
    }
    
    public void fire(String ename, Object... edata){
        Event e = new Event(ename);
        e.setAllFields(edata);
        
        fire(e);
    }
    
    public void register(String event, EventListener el){
        getListenerList(event).add(new WeakReference<EventListener>(el));
    }
    
    public void unregister(String event, final EventListener el){
        iterateListeners(event, new Fn1<Boolean, EventListener>() {

            @Override
            public Boolean invoke(EventListener arg) {
                return !arg.equals(el);
            }
        });
    }
}
