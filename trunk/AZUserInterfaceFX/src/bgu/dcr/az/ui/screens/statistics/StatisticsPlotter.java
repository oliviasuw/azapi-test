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
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author User
 */
public class StatisticsPlotter implements Plotter {

    private final BorderPane pane;
    private boolean asTable = false;

    public StatisticsPlotter(BorderPane pane) {
        this.pane = pane;
    }

    public void setAsTable(boolean asTable) {
        this.asTable = asTable;
    }

    @Override
    public void plotPieChart(Data data, String valueField, String seriesField, String title, String valueFieldLabel, String seriesFieldLabel) {
        if (asTable) {
            plotTable(data);
            return;
        }
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
        if (asTable) {
            plotTable(data);
            return;
        }
        NumberAxis xAxis = new NumberAxis();
        if (properties.getXAxisLabel() != null) {
            xAxis.setLabel(properties.getXAxisLabel());
        }

        ValueAxis yAxis;

        if (properties.isLogarithmicScale()) {
            yAxis = new LogarithmicNumberAxis();
        } else {
            yAxis = new NumberAxis();
        }

        if (properties.getYAxisLabel() != null) {
            yAxis.setLabel(properties.getYAxisLabel());
        }

        LineChart chart = new LineChart(xAxis, yAxis);
        if (properties.getTitle() != null) {
            chart.setTitle(properties.getTitle());
        }

        xAxis.setForceZeroInRange(false);
        fillXYData(data, chart, seriesField, xField, yField, false, false);

        pane.setCenter(chart);
    }

    private void fillXYData(Data data, XYChart chart, String seriesField, String xField, String yField, boolean xIsCategory, boolean yIsCategory) {
        Map<String, XYChart.Series> series = new HashMap<>();
//        ArrayList<XYChart.Data> result = new ArrayList<>();
        double maxY = 0;

        for (RecordAccessor r : data) {
            final String seriesName = seriesField == null ? "" : r.getString(seriesField);
            XYChart.Series s = series.get(seriesName);
            if (s == null) {
                s = new XYChart.Series();
                s.setName(seriesName);
                series.put(seriesName, s);
            }

            Object x = xIsCategory ? r.getString(xField) : r.getDouble(xField);
            Object y = yIsCategory ? r.getString(yField) : r.getDouble(yField);

            maxY = Math.max(maxY, (Double) y);

            s.getData().add(new XYChart.Data<>(x, y));
        }

        if (!xIsCategory && !yIsCategory) {
            series.values().forEach(s
                    -> FXCollections.<Object>sort(s.getData(), (o1, o2)
                            -> ((Double) ((XYChart.Data) o1).getXValue()).compareTo((Double) ((XYChart.Data) o2).getXValue())));
        }

        if (chart.getYAxis() instanceof LogarithmicNumberAxis) {
            ((LogarithmicNumberAxis) chart.getYAxis()).setLogarithmizedUpperBound(maxY);
        }
        series.values().forEach(chart.getData()::add);
    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties) {
        if (asTable) {
            plotTable(data);
            return;
        }

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
            xAxis.setLabel(properties.getCategoryAxisLabel());
        }

        if (properties.getValueFieldLabel() != null) {
            yAxis.setLabel(properties.getValueFieldLabel());
        }

        if (properties.isHorizontal()) {
            bar = new BarChart(yAxis, xAxis);
            fillXYData(data, bar, seriesField, valueField, categoryField, false, true);
        } else {
            bar = new BarChart(xAxis, yAxis);
            fillXYData(data, bar, seriesField, categoryField, valueField, true, false);
        }

        if (properties.getCaption() != null) {
            bar.setTitle(properties.getCaption());
        }

        bar.setLegendVisible(seriesField != null);
        pane.setCenter(bar);

    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField) {
        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        plotLineChart(data, xField, yField, seriesField, properties);
    }

    private void plotTable(Data data) {
        final TableView table = StatisticViewUtils.createTable(data);
        table.getStyleClass().add("dark");
        pane.setCenter(table);
    }

}
