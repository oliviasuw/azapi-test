/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LineChart.java
 *
 * Created on 27/11/2011, 18:38:24
 */
package bc.ui.swing.charts;

import bgu.csp.az.api.infra.stat.vmod.LineVisualModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map.Entry;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bennyl
 */
public class LineChart extends javax.swing.JPanel {

    ChartPanel chartPanel;
    private XYLineAndShapeRenderer renderer;

    /** Creates new form LineChart */
    public LineChart() {
        initComponents();
    }

    public void setModel(LineVisualModel line) {
        XYDataset dataset = createDataset(line);
        JFreeChart chart = ChartFactory.createXYLineChart(
                line.getTitle(), // chart title
                line.getxAxisName(), // x axis label
                line.getyAxisName(), // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
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


        removeAll();
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(202, 224, 195));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private XYDataset createDataset(LineVisualModel line) {
        XYSeries series1 = new XYSeries(""); //HERE SHOULD BE THE ALGORITHM NAME
        for (Entry<Double, Double> v : line.getValues().entrySet()) {
            series1.add(v.getKey(), v.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;
    }

    public static void main(String[] args) {
        JFrame j = new JFrame();
        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LineChart chart = new LineChart();
        LineVisualModel model = new LineVisualModel("aaaa", "bbbb", "ccccc");
        model.setPoint(5, 5);
        model.setPoint(7, 8);
        model.setPoint(4, 9);
        model.setPoint(12, 5);
        chart.setModel(model);
        j.setContentPane(chart);
        j.pack();
        j.setVisible(true);
    }
}
