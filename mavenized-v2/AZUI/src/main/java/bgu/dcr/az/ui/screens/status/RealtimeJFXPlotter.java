/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.status;

import bgu.dcr.az.mas.stat.AdditionalBarChartProperties;
import bgu.dcr.az.mas.stat.AdditionalLineChartProperties;
import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.RecordAccessor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author User
 */
public class RealtimeJFXPlotter implements Plotter {

    BorderPane plotContainer;
    Chart innerChart;

    public RealtimeJFXPlotter(BorderPane plotContainer) {
        this.plotContainer = plotContainer;
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void plotPieChart(Data data, String valueField, String seriesField, String title, String valueFieldLabel, String seriesFieldLabel) {
        PieChart pie;
        if (innerChart instanceof PieChart) {
            pie = (PieChart) innerChart;

            int idx = 0;
            if (data.numRecords() <= 1 && data.numRecords() == pie.getData().size()) {
                return;
            }

            for (RecordAccessor r : data) {
                if (pie.getData().size() > idx) {
                    pie.getData().get(idx++).setPieValue(r.getDouble(valueField));
                } else {
                    idx++;
                    pie.getData().add(new PieChart.Data(r.getString(seriesField), r.getDouble(valueField)));
                }
            }

        } else {
            pie = new PieChart();

            innerChart = pie;
            for (RecordAccessor r : data) {
                pie.getData().add(new PieChart.Data(r.getString(seriesField), r.getDouble(valueField)));
            }

            pie.setLabelsVisible(true);
            pie.setLegendVisible(false);
            plotContainer.setCenter(innerChart);

        }

    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties) {
        BarChart bar;

        if (innerChart instanceof BarChart) {
            bar = (BarChart) innerChart;

            final XYChart.Series<Object, Double> series = (XYChart.Series) bar.getData().get(0);

            int idx = 0;
            for (RecordAccessor r : data) {
                if (series.getData().size() > idx) {

                    final Double value = r.getDouble(valueField);
                    if (properties.isHorizontal()) {
                        final XYChart.Data<Object, Double> d = series.getData().get(idx++);
                        if (value.compareTo((Double) d.getXValue()) != 0) {
                            d.setXValue(value);
                        }
                    } else {
                        final XYChart.Data<Object, Double> d = series.getData().get(idx++);
                        if (value.compareTo((Double) d.getYValue()) != 0) {
                            d.setYValue(value);
                        }
                    }
                } else {
                    idx++;
                    if (properties.isHorizontal()) {
                        series.getData().add(new XYChart.Data(r.getDouble(valueField), r.getString(categoryField)));
                    } else {
                        series.getData().add(new XYChart.Data(r.getString(categoryField), r.getDouble(valueField)));
                    }
                }
            }

        } else {
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

            innerChart = bar;
            bar.setLegendVisible(false);
            plotContainer.setCenter(innerChart);
        }
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, AdditionalLineChartProperties properties) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
