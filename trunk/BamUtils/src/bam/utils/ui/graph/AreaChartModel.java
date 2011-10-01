/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

import java.util.HashMap;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public class AreaChartModel extends ChartModel {

    XYSeriesCollection chartModel;
    private XYSeries defaultSeries;
    private TimeSeriesCollection dateDataset;
    private HashMap<Float, Float> xy;
    private HashMap<Float, Long> xHits;
    public boolean logarithmicRangeScale = false;
    
    
    public AreaChartModel(String title, String xAxeTitle, String yAxeTitle) {
        super(title, xAxeTitle, yAxeTitle);
        chartModel = new XYSeriesCollection();
        defaultSeries = new XYSeries("");
        chartModel.addSeries(defaultSeries);
        xy = new HashMap<Float, Float>();
        xHits = new HashMap<Float, Long>();
    }

    public boolean isLogarithmicRangeScale() {
        return logarithmicRangeScale;
    }

    public void setLogarithmicRangeScale(boolean logarithmicRangeScale) {
        this.logarithmicRangeScale = logarithmicRangeScale;
    }

    public XYSeriesCollection getChartModel() {
        return chartModel;
    }

    public void set(float x, float y) {
        defaultSeries.addOrUpdate(x, y);
        xy.put(x, y);
    }

    public float get(float x) {
        return xy.containsKey(x) ? xy.get(x) : 0;
    }

    public void avg(float x, float y) {
        long hits = xHits.containsKey(x) ? xHits.get(x) : 0;
        float now = get(x);
        float all = now * hits + y;
        float avg = all / (hits + 1);
        set(x, avg);
        this.xHits.put(x, xHits.containsKey(x) ? xHits.get(x) : 1);
    }
}
