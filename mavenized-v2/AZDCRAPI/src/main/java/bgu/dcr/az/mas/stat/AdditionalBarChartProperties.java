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
public class AdditionalBarChartProperties {

    private String caption;
    private String categoryFieldLabel;
    private String valueFieldLabel;
    private boolean horizontal;
    private Integer maxValue = null;

    public String getCaption() {
        return caption;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public void setTitle(String caption) {
        this.caption = caption;
    }

    public String getCategoryAxisLabel() {
        return categoryFieldLabel;
    }

    public void setCategoryAxisLabel(String axisFieldLabel) {
        this.categoryFieldLabel = axisFieldLabel;
    }

    public String getValueFieldLabel() {
        return valueFieldLabel;
    }

    public void setValueFieldLabel(String valueFieldLabel) {
        this.valueFieldLabel = valueFieldLabel;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

}
