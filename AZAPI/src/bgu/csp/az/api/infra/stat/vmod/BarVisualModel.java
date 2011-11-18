/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat.vmod;

import bgu.csp.az.api.infra.stat.VisualModel;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class BarVisualModel implements VisualModel{
    private Map<String, Double> values = new LinkedHashMap<String, Double>();
    private String title;

    public BarVisualModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    public void putBar(String barName, double value){
        values.put(barName, value);
    }

    public Map<String, Double> getValues() {
        return values;
    }
    
}
