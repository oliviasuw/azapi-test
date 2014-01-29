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

    void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel);

    void plotBarChart(Data data, String lableField, String valueField, String seriesField, String title, String xAxisLabel, String yAxisLabel);

    void plotPieChart(Data data, String lableField, String seriesField, String title, String lableFieldLabel, String seriesFieldLabel);
}
