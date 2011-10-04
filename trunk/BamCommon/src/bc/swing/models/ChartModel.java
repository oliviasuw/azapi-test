/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models;

import bc.swing.pfrm.viewtypes.ChartType;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public class ChartModel {
    private Dataset dataset;
    private String title = "Untitled";
    private String domainAxisLabel = "X";
    private String rangeAxisLabel = "Y";
    private ChartType ctype;
    private boolean useLogarithmicRange = false;
    
    public ChartModel(ChartType ctype) {
        this.dataset = ctype.createDataset();
        this.ctype = ctype;
    }
    
    public ChartType getChartType() {
        return ctype;
    }

    public void setChartType(ChartType ctype) {
        this.ctype = ctype;
    }

    public String getTitle() {
        return title;
    }

    public String getDomainAxisLabel() {
        return domainAxisLabel;
    }

    public String getRangeAxisLabel() {
        return rangeAxisLabel;
    }

    public boolean isUseLogarithmicRange() {
        return useLogarithmicRange;
    }

    public void setUseLogarithmicRange(boolean useLogarithmicRange) {
        this.useLogarithmicRange = useLogarithmicRange;
    }

    public Dataset getDataset() {
        return dataset;
    }
    
    public void add(double x, double y){
        if (dataset instanceof XYSeriesCollection){ //sutable to area-chart
            add((XYSeriesCollection)dataset, x, y);
        }
    }

    private void add(XYSeriesCollection ds, double x, double y) {
        XYSeries series = ds.getSeries("");
        if (series == null){
            series = new XYSeries("");
            ds.addSeries(series);
        }        
        
        series.add(x, y);
    }
}
