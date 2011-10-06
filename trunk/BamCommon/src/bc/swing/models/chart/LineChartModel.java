/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models.chart;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public class LineChartModel extends ChartModel {

    public LineChartModel() {
        super(new XYSeriesCollection());
    }

    @Override
    public XYSeriesCollection getDataset() {
        return (XYSeriesCollection) super.getDataset();
    }

    @Override
    public JFreeChart generateView() {
        JFreeChart ret = ChartFactory.createXYLineChart(
                getTitle(),
                getDomainAxisLabel(),
                getRangeAxisLabel(),
                getDataset(),
                PlotOrientation.VERTICAL,
                false,
                false,
                false);

        XYPlot plot = ret.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(160, 200, 255));

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickMarksVisible(true);

        plot.setForegroundAlpha(1f);
        plot.setBackgroundPaint(Color.WHITE);

        if (isUseLogarithmicRange()) {
            NumberAxis rangeAxis = new LogarithmicAxis("Log(" + getRangeAxisLabel() + ")");
            plot.setRangeAxis(rangeAxis);
        } else {
            NumberAxis rangeAxis = new NumberAxis(getRangeAxisLabel());
            plot.setRangeAxis(rangeAxis);
        }

        return ret;
    }

    public void add(double x, double y) {
        XYSeries series;
        if (getDataset().getSeriesCount() == 0) {
            series = new XYSeries("");
            getDataset().addSeries(series);
        } else {
            series = getDataset().getSeries(0);
        }

        series.add(x, y);
    }
}
