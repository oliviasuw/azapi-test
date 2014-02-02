/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.charts;

import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.RecordAccessor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author User
 */
public class JFreeChartPlotter implements Plotter {

    private JPanel chartPanel;

    public JFreeChartPlotter(JPanel chartPanel) {
        this.chartPanel = chartPanel;
        chartPanel.setLayout(new BorderLayout());
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        XYLineAndShapeRenderer renderer;

        XYDataset dataset = createLineChartDataset(data, xField, yField, seriesField);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title, // chart title
                xAxisLabel, // x axis label
                yAxisLabel, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        renderer.setSeriesPaint(0, new Color(153, 215, 255));

        setChart(chart);
    }

    private XYDataset createLineChartDataset(Data data, String xField, String yField, String seriesField) {

        Collection<String> series = findSeries(data, seriesField);
        XYSeries[] seriesDataset = new XYSeries[series.size()];

        int i = 0;
        for (String s : series) {
            XYSeries series1 = new XYSeries(s);

            for (RecordAccessor d : data) {
                series1.add(d.getDouble(xField), d.getDouble(yField));
            }

            seriesDataset[i++] = series1;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries s : seriesDataset) {
            dataset.addSeries(s);
        }
        return dataset;
    }

    @Override
    public void plotBarChart(Data data, String lableField, String valueField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        DefaultCategoryDataset dataset = createBarChartDataset(data, lableField, valueField, seriesField);
        JFreeChart ret = ChartFactory.createBarChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        ret.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) ret.getPlot();
        plot.setBackgroundPaint(Color.white);

        plot.setDomainGridlinePaint(new Color(200, 200, 200));
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinesVisible(true);
        BarRenderer barrenderer = (BarRenderer) plot.getRenderer();

        barrenderer.setDrawBarOutline(true);
        barrenderer.setBarPainter(new StandardBarPainter());

        final Color baseColor = new Color(160, 200, 255);
        final DefaultDrawingSupplier otherColors = new DefaultDrawingSupplier();
        plot.setDrawingSupplier(new DefaultDrawingSupplier() {

            boolean first = true;

            @Override
            public Paint getNextPaint() {
                if (first) {
                    first = false;
                    return baseColor;
                }
                return otherColors.getNextPaint();
            }
        });

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickMarksVisible(true);

        setChart(ret);
    }

    private DefaultCategoryDataset createBarChartDataset(Data data, String lableField, String valueField, String seriesField) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Collection<String> series = findSeries(data, seriesField);
        for (String s : series) {
            for (RecordAccessor d : data){
                dataset.addValue(d.getDouble(lableField), s, d.getDouble(valueField));
            }
        }
        
        return dataset;
    }

    @Override
    public void plotPieChart(Data data, String lableField, String valueField, String title, String xAxisLabel, String yAxisLabel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Collection<String> findSeries(Data data, String seriesField) {
        Set<String> series = new HashSet<>();

        for (RecordAccessor d : data) {
            series.add(d.getString(seriesField));
        }

        return series;
    }

    private void setChart(JFreeChart chart) {
        ChartPanel c = new ChartPanel(chart);
        chartPanel.add(c, BorderLayout.CENTER);
        chartPanel.invalidate();
        chartPanel.revalidate();
        chartPanel.repaint();
    }

}
