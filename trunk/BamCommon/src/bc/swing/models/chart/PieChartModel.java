/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models.chart;

import java.awt.Color;
import java.awt.Paint;
import java.text.AttributedString;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 *
 * @author bennyl
 */
public class PieChartModel extends ChartModel{

    public PieChartModel() {
        super(new DefaultPieDataset());
    }

    @Override
    public DefaultPieDataset getDataset() {
        return (DefaultPieDataset) super.getDataset();
    }

    public void set(String name, double value){
        getDataset().setValue(name, value);
    }
    
    @Override
    public JFreeChart generateView() {
        JFreeChart chart = ChartFactory.createPieChart3D(
            getTitle(),  // chart title
            getDataset(),                // data
            false,                   // include legend
            true,
            false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data to display");
        //return chart;
        
        chart.setBackgroundPaint(Color.white);
        plot.setBackgroundPaint(Color.white);
        
        plot.setDrawingSupplier(new DefaultDrawingSupplier(){

            @Override
            public Paint getNextPaint() {
                return new Color((int) (Math.random()*60) + 100, (int) (Math.random()*50) + 150, (int) (Math.random()*55) + 200);
                //return baseColor;
            }
            
        });
        
        plot.setLabelGenerator(new PieSectionLabelGenerator() {

            public String generateSectionLabel(PieDataset pd, Comparable cmprbl) {
                return cmprbl + " (" +  pd.getValue(cmprbl) + " Liters)";
            }

            public AttributedString generateAttributedSectionLabel(PieDataset pd, Comparable cmprbl) {
                return new AttributedString(""+pd.getValue(cmprbl));
            }
        });
        
        return chart;
        
    }
    
}
