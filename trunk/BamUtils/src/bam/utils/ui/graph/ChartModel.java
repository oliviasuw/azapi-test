/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

/**
 *
 * @author bennyl
 */
public class ChartModel {

    String title;
    String xAxeTitle;
    String yAxeTitle;
    boolean hasLegend = true;

    public ChartModel(String title, String xAxeTitle, String yAxeTitle) {
        this.title = title;
        this.xAxeTitle = xAxeTitle;
        this.yAxeTitle = yAxeTitle;
    }

    public boolean isHasLegend() {
        return hasLegend;
    }

    public void setHasLegend(boolean hasLegend) {
        this.hasLegend = hasLegend;
    }
    
    public String getChartTitle() {
        return title;
    }

    public String getxAxeTitle() {
        return xAxeTitle;
    }

    public String getyAxeTitle() {
        return yAxeTitle;
    }
}
