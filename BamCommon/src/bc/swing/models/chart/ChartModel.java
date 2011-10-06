/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 *
 * @author bennyl
 */
public abstract class ChartModel {
    private Dataset dataset;
    private String title = "Untitled";
    private String domainAxisLabel = "X";
    private String rangeAxisLabel = "Y";
    private boolean useLogarithmicRange = false;
    
    public ChartModel(Dataset dataset) {
        this.dataset = dataset;
        setDomainAxisLabel("X");
        setRangeAxisLabel("Y");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDomainAxisLabel(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    public void setRangeAxisLabel(String rangeAxisLabel) {
        this.rangeAxisLabel = rangeAxisLabel;
    }
    
    public abstract JFreeChart generateView();
    
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
    
}
