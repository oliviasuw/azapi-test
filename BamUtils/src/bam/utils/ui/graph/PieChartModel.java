/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author bennyl
 */
public class PieChartModel extends ChartModel {

    DefaultPieDataset chartModel = new DefaultPieDataset();
    
    public PieChartModel(String title, String xAxeTitle, String yAxeTitle) {
        super(title, xAxeTitle, yAxeTitle);
    }

    public void add(String name, long value){
        chartModel.setValue(name, value);
    }
    
    public DefaultPieDataset getChartModel() {
        return chartModel;
    }
    
}
