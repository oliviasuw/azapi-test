/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models.chart;

import java.awt.Color;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public class AreaChartModel extends ChartModel {

    HashMap<Double, Integer> hits = new HashMap<Double, Integer>();
    
    public AreaChartModel() {
        super(new XYSeriesCollection());
    }

    @Override
    public JFreeChart generateView() {
        JFreeChart ret = ChartFactory.createXYAreaChart(
                getTitle(),
                getDomainAxisLabel(),
                getRangeAxisLabel(),
                ((XYSeriesCollection) getDataset()),
                PlotOrientation.VERTICAL,
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

        if (isUseLogarithmicRange()) {
            NumberAxis rangeAxis = new LogarithmicAxis("Log(" + getRangeAxisLabel() + ")");
            plot.setRangeAxis(rangeAxis);
        } else {
            NumberAxis rangeAxis = new NumberAxis(getRangeAxisLabel());
            plot.setRangeAxis(rangeAxis);
        }

        return ret;
    }


    @Override
    public XYSeriesCollection getDataset() {
        return (XYSeriesCollection) super.getDataset();
    }
    
    public void add(double x, double y) {
        XYSeriesCollection ds = getDataset();
        XYSeries series;
        if (ds.getSeriesCount() == 0){
            series = new XYSeries("");
            ds.addSeries(series);
        }else {
            series = ds.getSeries(0);
        }

        series.add(x, y);
    }
//    
//    public double getY(double x){
//        getDataset().getSeries(0).ge
//    }
//    
//    public void avg(double x, double y){
//        int hit = (this.hits.containsKey(x)? this.hits.get(x): 0);
//        double cur = get
//    }
}
