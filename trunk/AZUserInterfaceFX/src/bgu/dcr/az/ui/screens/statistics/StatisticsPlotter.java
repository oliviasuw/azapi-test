/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.mas.stat.AdditionalBarChartProperties;
import bgu.dcr.az.mas.stat.AdditionalLineChartProperties;
import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.RecordAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author User
 */
public class StatisticsPlotter implements Plotter {

    private final BorderPane pane;

    public StatisticsPlotter(BorderPane pane) {
        this.pane = pane;
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

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, AdditionalLineChartProperties properties) {

        NumberAxis xAxis = new NumberAxis();
        if (properties.getXAxisLabel() != null) {
            xAxis.setLabel(properties.getXAxisLabel());
        }

        ValueAxis yAxis;

//        if (properties.isLogarithmicScale()) {
//
//            yAxis = new LogarithmicNumberAxis();
//        } else {
        yAxis = new NumberAxis();
//        }

        if (properties.getYAxisLabel() != null) {
            yAxis.setLabel(properties.getYAxisLabel());
        }

        LineChart chart = new LineChart(xAxis, yAxis);
        if (properties.getTitle() != null) {
            chart.setTitle(properties.getTitle());
        }

        xAxis.setForceZeroInRange(false);
        fillXYData(data, chart, seriesField, xField, yField, false);

        pane.setCenter(chart);
    }

    private void fillXYData(Data data, XYChart chart, String seriesField, String xField, String yField, boolean xIsCategory) {
        Map<String, XYChart.Series> series = new HashMap<>();
//        ArrayList<XYChart.Data> result = new ArrayList<>();

        for (RecordAccessor r : data) {
            final String seriesName = r.getString(seriesField);
            XYChart.Series s = series.get(seriesName);
            if (s == null) {
                s = new XYChart.Series();
                s.setName(seriesName);
                series.put(seriesName, s);
            }

            if (xIsCategory) {
                s.getData().add(new XYChart.Data<>(r.getString(xField), r.getDouble(yField)));
            } else {
                s.getData().add(new XYChart.Data<>(r.getDouble(xField), r.getDouble(yField)));
            }
        }
        
        if (!xIsCategory) {
            for (XYChart.Series s : series.values()) {
                FXCollections.<Object>sort(s.getData(), (o1, o2) -> {
                    return ((Double) ((XYChart.Data) o1).getXValue()).compareTo((Double) ((XYChart.Data) o2).getXValue());
                });
            }
        }
        
        for (XYChart.Series s : series.values()) {
            chart.getData().add(s);
        }

    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties) {
        BarChart bar;

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis;

        if (properties.getMaxValue() != null) {
            yAxis = new NumberAxis(0, properties.getMaxValue(), 1);
            yAxis.setAutoRanging(false);
        } else {
            yAxis = new NumberAxis();
        }

        if (properties.getCategoryAxisLabel() != null) {
            yAxis.setLabel(properties.getCategoryAxisLabel());
        }

        if (properties.getValueFieldLabel() != null) {
            xAxis.setLabel(properties.getValueFieldLabel());
        }

        if (properties.isHorizontal()) {
            bar = new BarChart(yAxis, xAxis);
        } else {
            bar = new BarChart(xAxis, yAxis);
        }

        fillXYData(data, bar, seriesField, valueField, categoryField, true);

        if (properties.getCaption() != null) {
            bar.setTitle(properties.getCaption());
        }

        bar.setLegendVisible(false);
        pane.setCenter(bar);

    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField) {
        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        plotLineChart(data, xField, yField, seriesField, properties);
    }

}
