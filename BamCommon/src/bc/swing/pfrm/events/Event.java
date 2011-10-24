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
    
    public boolean isNamed(String name){
        return this.name.equals(name);
    }
    
    public void setAllFields(Object[] data){
        for (int i=0; i<data.length; i+=2){
            this.data.put(data[i].toString(), data[i+1]);
        }
    }
    
    public void setAllFields(String fname1, Object fval1, Object... rest){
        this.data.put(fname1, fval1);
        setAllFields(rest);
    }
    
    public Object getField(String field){
        return data.get(field);
    }

    public <T> T getField(String field, Class<T> cls){
        Object ret = data.get(field);
        if (ret == null){
            return null;
        }
        
        if (cls.isAssignableFrom(ret.getClass())){
            return (T) ret;
        }else {
            System.err.println("Event requested to get field " + field + " as class " + cls.getSimpleName() + " but real class is " + ret.getClass().getSimpleName() + " -> returning null.");
            return null;
        }
    }
    
    public String getStringField(String field){
        return getField(field, String.class);
    }
    
    public String getName() {
        return name;
    }
    
    
}
