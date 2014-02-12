/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.stat;

/**
 *
 * @author User
 */
public class AdditionalLineChartProperties {

    String xAxisLabel = null;
    String yAxisLabel = null;
    String title = null;
    boolean logarithmicScale = false;

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public void setLogarithmicScale(boolean logarithmicScale) {
        this.logarithmicScale = logarithmicScale;
    }

    public boolean isLogarithmicScale() {
        return logarithmicScale;
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
