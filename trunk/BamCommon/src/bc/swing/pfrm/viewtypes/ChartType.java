/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.viewtypes;

import bc.swing.models.ChartModel;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public enum ChartType {

    SINGLE_AREA_CHART {

        @Override
        public JFreeChart getChart(ChartModel model) {
            JFreeChart ret = ChartFactory.createAreaChart(
                    model.getTitle(),
                    model.getDomainAxisLabel(),
                    model.getRangeAxisLabel(),
                    (CategoryDataset) model.getDataset(),
                    PlotOrientation.HORIZONTAL,
                    false, //Legend
                    false, //Tooltips
                    false //URLs
                    );

            XYPlot plot = ret.getXYPlot();
            XYAreaRenderer2 renderer = new XYAreaRenderer2();
            plot.setRenderer(renderer);
            renderer.setAutoPopulateSeriesOutlinePaint(false);
            renderer.setOutline(false);
            renderer.setAutoPopulateSeriesFillPaint(true);
            renderer.setSeriesPaint(0, new Color(160, 200, 255));

            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(new Color(200, 200, 200));
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(200, 200, 200));
            ValueAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickMarksVisible(true);

            plot.setForegroundAlpha(1f);
            plot.setBackgroundPaint(Color.WHITE);

            if (model.isUseLogarithmicRange()) {
                NumberAxis rangeAxis = new LogarithmicAxis("Log(" + model.getRangeAxisLabel() + ")");
                plot.setRangeAxis(rangeAxis);
            } else {
                NumberAxis rangeAxis = new NumberAxis(model.getRangeAxisLabel());
                plot.setRangeAxis(rangeAxis);
            }

            return ret;
        }

        @Override
        public Dataset createDataset() {
            return new XYSeriesCollection();
        }
        
    };

    public abstract JFreeChart getChart(ChartModel model);
    public abstract Dataset createDataset();
}
