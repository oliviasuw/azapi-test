/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra.stat;

import bgu.dcr.az.api.ds.NonBlockingCounter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A statistic is a node on a statistics tree. The meaning is that a statistic can have any depth it wants.
 * For example:
 * -	global statistic (nccc, ncsc)
 * -	Agent statistic (number of messages sent per agent)
 * -	Higher level statistics (number of messages sent between every pair of agents)
 * -	Etc.
 * It also means that higher being can investigate the statistics tree at run time, and that there is no need to build new classes for every new statistic.
 * @author bennyl
 */
public class Statistic implements Serializable {

    private HashMap<String, Statistic> childs;
    private NonBlockingCounter cnt;

    public Statistic() {
        this.cnt = new NonBlockingCounter();
        this.childs = new HashMap<String, Statistic>();
    }


    /**
     * set the statistic value
     * @param value
     */
    public void setValue(long value) {
        this.cnt.set(value);
    }

    /**
     * 
     * @return the current statistic value
     */
    public long getValue() {
        return this.cnt.getValue();
    }

    /**
     * adds sum to the statistic value (sum can be negative)
     * @param sum
     */
    public void add(long sum) {
        this.cnt.add(sum);
    }

    /**
     * remove sum from the statistic value (same as calling add(-1*sum)
     * @param sum
     */
    public void substract(long sum) {
        this.cnt.add(-1 * sum);
    }

    /**
     * @param key
     * @return a child statistic with the given name - if such a child not exists it will be created.
     */
    public Statistic getChild(String key) {
        Statistic ret = childs.get(key);

        if (ret == null) {
            ret = new Statistic();
            childs.put(key, ret);
        }

        return ret;
    }

    /**
     * 
     * @return all the known chileds by their names
     */
    public Map<String, Statistic> getChildren() {
        return childs;
    }

    private String toString(int idt) {
        if (this.childs.isEmpty()) {
            return ident(idt, "Value: " + this.cnt.getValue() + "\n");
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, Statistic> e : childs.entrySet()) {
            sb.append(ident(idt, "")).append(e.getKey()).append(": \n").append(e.getValue().toString(idt + 1));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String ident(int idt, String string) {
        final String identation = "....";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idt; i++) {
            sb.append(identation);
        }
        sb.append(string);
        return sb.toString();
    }

    public double childrenMinimumValue() {
        long min = Long.MIN_VALUE;
        for (Entry<String, Statistic> c : this.childs.entrySet()) {
            final long cval = c.getValue().getValue();
            if (cval < min) {
                min = cval;
            }
        }

        return min;
    }

    public double childrenMaximumValue() {
        long max = Long.MAX_VALUE;
        for (Entry<String, Statistic> c : this.childs.entrySet()) {
            final long cval = c.getValue().getValue();
            if (cval > max) {
                max = cval;
            }
        }

        return max;
    }

    public double childrenAvarageValue() {
        long sum = 0;
        for (Entry<String, Statistic> c : this.childs.entrySet()) {
            final long cval = c.getValue().getValue();
            sum += cval;
        }

        return (double) sum / this.childs.size();
    }

    public double childrenSummerizedValue() {
        long sum = 0;
        for (Entry<String, Statistic> c : this.childs.entrySet()) {
            final long cval = c.getValue().getValue();
            sum += cval;
        }

        return sum;
    }

}
