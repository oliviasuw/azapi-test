/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.stat;

import bgu.dcr.az.orm.api.Data;

/**
 *
 * @author User
 */
public interface Plotter {

    void plotLineChart(Data data, String xField, String yField, String seriesField, AdditionalLineChartProperties properties);

    void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties);

    void plotPieChart(Data data, String valueField, String seriesField, String title, String valueFieldLabel, String categoriesFieldLabel);

    default void plotBarChart(Data data, String categoryField, String valueField, AdditionalBarChartProperties properties) {
        plotBarChart(data, categoryField, valueField, null, properties);
    }

    default void plotBarChart(Data data, String categoryField, String valueField, String seriesField) {
        plotBarChart(data, categoryField, valueField, seriesField, new AdditionalBarChartProperties());
    }

    default void plotBarChart(Data data, String categoryField, String valueField) {
        plotBarChart(data, categoryField, valueField, new AdditionalBarChartProperties());
    }

    default void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(title);
        properties.setXAxisLabel(xAxisLabel);
        properties.setYAxisLabel(yAxisLabel);

        plotLineChart(data, xField, yField, seriesField, properties);
    }

    default void plotLineChart(Data data, String xField, String yField, String seriesField) {
        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        plotLineChart(data, xField, yField, seriesField, properties);
    }

}
