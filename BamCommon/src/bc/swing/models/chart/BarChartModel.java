/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author bennyl
 */
public class BarChartModel extends ChartModel {

    HashMap<String, Long> barHits = new HashMap<String, Long>(); //used for avg    

    public BarChartModel() {
        super(new DefaultCategoryDataset());
    }

    public void set(String bar, double value) {
        getDataset().addValue(value, "", bar);

    }

    @Override
    public DefaultCategoryDataset getDataset() {
        return (DefaultCategoryDataset) super.getDataset();
    }

    /**
     * add the given value for the bar - and cause the bar to show the avarege of all the values
     * dont mix set and avg as set bypassing the avarage counters
     * @param bar
     * @param add 
     */
    public void avg(String bar, long add) {
        long hits = barHits.containsKey(bar) ? barHits.get(bar) : 0;
        double now = 0L;
        now = (hits == 0 ? 0L : (Double) (getDataset().getValue("", bar)));
        double all = now * hits + add;
        double avg = all / (hits + 1);
        set(bar, avg);
        this.barHits.put(bar, hits + 1);
    }

    @Override
    public JFreeChart generateView() {
        JFreeChart ret = ChartFactory.createBarChart(
                getTitle(),
                getDomainAxisLabel(),
                getRangeAxisLabel(),
                ((DefaultCategoryDataset) getDataset()),
                PlotOrientation.VERTICAL,
                false,
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
        plot.setDrawingSupplier(new DefaultDrawingSupplier(){

            @Override
            public Paint getNextPaint() {
                return baseColor;
            }
            
        });
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickMarksVisible(true);

        return ret;
    }
}
