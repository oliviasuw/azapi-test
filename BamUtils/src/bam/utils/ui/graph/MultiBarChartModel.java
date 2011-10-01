/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author bennyl
 */
public class MultiBarChartModel extends ChartModel{

    DefaultCategoryDataset chartModel;
    
    public MultiBarChartModel(String title, String xAxeTitle, String yAxeTitle) {
        super(title, xAxeTitle, yAxeTitle);
        chartModel = new DefaultCategoryDataset();
    }
    
    public void set(String category, String bar, long data){
        chartModel.addValue(data, bar, category);
    }

    public CategoryDataset getChartModel() {
        return chartModel;
    }
    
}
