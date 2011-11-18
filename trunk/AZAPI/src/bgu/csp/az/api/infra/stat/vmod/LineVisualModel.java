/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat.vmod;

import bgu.csp.az.api.infra.stat.VisualModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class LineVisualModel implements VisualModel{
    String xAxisName;
    String yAxisName;
    String title;
    Map<Double, Double> values = new HashMap<Double, Double>();

    public LineVisualModel(String xAxisName, String yAxisName, String title) {
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.title = title;
    }
    
    public void setPoint(double x, double y){
        values.put(x, y);
    }

    public Map<Double, Double> getValues() {
        return values;
    }

    public String getTitle() {
        return title;
    }

    public String getxAxisName() {
        return xAxisName;
    }

    public String getyAxisName() {
        return yAxisName;
    }
    
}
