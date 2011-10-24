/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.agt;

import bgu.csp.az.api.DeepCopyable;
import bgu.csp.az.api.Message;
import bgu.csp.az.utils.DeepCopyUtil;
import java.util.HashMap;

/**
 * the message that used by the SimpleAgent - its filled at runtime using annotations.
 * This message can contain any type of content, thus removes the need of building new types of messages for each algorithm.
 * @author bennyl
 */
public class SimpleMessage extends Message {

    private Object[] args;

    /**
     * Constract a simple message 
     * @param name the name of the message 
     * @param from the message sender
     * @param args the array of arguments this message contains
     */
    public SimpleMessage(String name, int from, Object[] args) {
        super(name, from);
        this.args = args;
    }

    /**
     * @return the arguments of this message
     */
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Message copy() {
        Object[] cargs = new Object[this.args.length]; 
        for (int i=0; i<args.length; i++){
            Object a = args[i];
            if (a instanceof DeepCopyable){
                cargs[i] = ((DeepCopyable)a).deepCopy();
            }else {
                cargs[i] = DeepCopyUtil.deepCopy(a);
            }
        }
        SimpleMessage ret = new SimpleMessage(getName(), getSender(), cargs);
        ret.metadata = new HashMap<String, Object> (metadata);
        ret.flag = flag;
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object a : args){
            sb.append(a.toString()).append(", ");
        }
        return "[" + getName() + (args.length > 0? ": " + sb.deleteCharAt(sb.length()-2).toString() + "]" : "]");
    }
    
}
