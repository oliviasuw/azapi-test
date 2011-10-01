/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author bennyl
 */
public class BarChartModel extends ChartModel{
    HashMap<String, Long> bars = new HashMap<String, Long>();
    HashMap<String, Long> barHits = new HashMap<String, Long>(); //used for avg
    List<Listener> listeners = new LinkedList<Listener>();
    DefaultCategoryDataset chartModel;
    
    public BarChartModel(String title, String xAxeTitle, String yAxeTitle) {
        super(title, xAxeTitle, yAxeTitle);
        chartModel = new DefaultCategoryDataset();
    }
    
    public void addListener(Listener listener){
        this.listeners.add(listener);
    }
    
    public void set(String bar, long value){
        final Long obar = this.bars.get(bar);
        this.bars.put(bar, value);
        
        chartModel.addValue(value, "", bar);
        if (obar == null || obar != value){
            fireBarValueChanged(bar, value);
        }
        if (obar == null){
            fireBarAdded(bar);
        }
        
    }
    
    /**
     * add the given value for the bar - and cause the bar to show the avarege of all the values
     * dont mix set and avg as set bypassing the avarage counters
     * @param bar
     * @param add 
     */
    public void avg(String bar, long add){
        long hits = barHits.containsKey(bar)? barHits.get(bar): 0;
        long now = get(bar);
        long all = now*hits + add;
        long avg = all/(hits+1);
        set(bar, avg);
        this.barHits.put(bar, this.barHits.containsKey(bar)? this.barHits.get(bar) + 1: 1);
    }
    
    public Long get(String bar){
        return bars.containsKey(bar)? bars.get(bar): 0;
    }
    
    public String[] getBars(){
        return bars.keySet().toArray(new String[0]);
    }

    private void fireBarValueChanged(String bar, long value) {
        for (Listener l : listeners) l.onBarChanged(this, bar, value);
    }

    private void fireBarAdded(String bar) {
        for (Listener l : listeners) l.onBarAdded(this, bar);
    }

    public DefaultCategoryDataset getChartModel() {
        return chartModel;
    }
    
    public static interface Listener{
        void onBarChanged(BarChartModel source, String barName, long newValue);
        void onBarAdded(BarChartModel source, String barName);
    }
}
