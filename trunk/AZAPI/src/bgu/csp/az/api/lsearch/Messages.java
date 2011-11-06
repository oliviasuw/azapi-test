/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.lsearch;

import bgu.csp.az.api.Message;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class Messages {
    List<Message> all;

    public Messages(List<Message> all) {
        this.all = all;
    }

    public <T> T[] allOf(Class<T> type){
        List<T> temp = new LinkedList<T>();
        for (Message a : all){
            if (type == a.getClass()) temp.add((T)a);
        }
        
        return temp.toArray((T[])Array.newInstance(type, 0));
    }

    public List<Message> getAll() {
        return all;
    }
    
}
