/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.events;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class Event {
    String name;
    Map<String,Object> data;
    
    public Event(String name) {
        this.data = new HashMap<String, Object>();
        this.name = name;
    }
    
    public void setAllFields(Object[] data){
        for (int i=0; i<data.length; i+=2){
            this.data.put(data[i].toString(), data[i+1]);
        }
    }
    
    public Object getField(String field){
        return data.get(field);
    }

    public String getName() {
        return name;
    }
    
    
}
