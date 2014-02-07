/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.mas.stat.AdditionalBarChartProperties;
import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.RecordAccessor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author User
 */
public class StatisticsPlotter implements Plotter {

    BorderPane pane;

    public StatisticsPlotter(BorderPane pane) {
        this.pane = pane;
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField, AdditionalBarChartProperties properties) {
        BarChart bar;

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis;

        if (properties.getMaxValue() != null) {
            yAxis = new NumberAxis(0, properties.getMaxValue(), 1);
            yAxis.setAutoRanging(false);
        } else {
            yAxis = new NumberAxis();
        }

        if (properties.isHorizontal()) {
            bar = new BarChart(yAxis, xAxis);
        } else {
            bar = new BarChart(xAxis, yAxis);
        }

        XYChart.Series series = new XYChart.Series();
        bar.getData().add(series);
        for (RecordAccessor r : data) {
            if (properties.isHorizontal()) {
                series.getData().add(new XYChart.Data(r.getDouble(valueField), r.getString(categoryField)));
            } else {
                series.getData().add(new XYChart.Data(r.getString(categoryField), r.getDouble(valueField)));
            }
        }

        if (properties.getCaption() != null) {
            bar.setTitle(properties.getCaption());
        }
        
        bar.setLegendVisible(false);
        pane.setCenter(bar);

    }

    @Override
    public void plotPieChart(Data data, String valueField, String seriesField, String title, String valueFieldLabel, String seriesFieldLabel) {
        PieChart pie = new PieChart();

        pie.setTitle(title);

        for (RecordAccessor r : data) {
            pie.getData().add(new PieChart.Data(r.getString(seriesField), r.getDouble(valueField)));
        }

        pie.setLabelsVisible(true);
        pie.setLegendVisible(false);
        pane.setCenter(pie);
    }

}
