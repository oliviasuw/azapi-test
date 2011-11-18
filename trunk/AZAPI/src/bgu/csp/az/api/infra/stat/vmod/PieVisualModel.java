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
public class PieVisualModel implements VisualModel{
    private Map<String, Double> values = new LinkedHashMap<String, Double>();
    private String title;

    public PieVisualModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    public void putSlice(String sliceName, double value){
        values.put(sliceName, value);
    }

    public Map<String, Double> getValues() {
        return values;
    }
    
}
