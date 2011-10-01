/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.graph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author bennyl
 */
public class TimeBasedAreaChartModel extends ChartModel {

    TimeSeriesCollection dataset = new TimeSeriesCollection();
    HashMap<String, TimeSeries> series = new HashMap<String, TimeSeries>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TimeBasedAreaChartModel(String title, String xAxeTitle, String yAxeTitle) {
        
        super(title, xAxeTitle, yAxeTitle);
    }
    
    public TimeSeriesCollection getChartModel(){
        return dataset;
    }
    
    private TimeSeries addArea(String name){
        TimeSeries series1 = new TimeSeries(name);
        dataset.addSeries(series1);
        series.put(name, series1);
        return series1;
    }
    
    public void set(String area, Date d, long data){
        TimeSeries s = series.get(area);
        if (s == null){
            s = addArea(area);
        }
        
        s.addOrUpdate(new Day(d), data);
    }

    public void setDateFormatter(SimpleDateFormat f) {
        dateFormat = f;
    }
    
    public DateFormat getDateFormatter() {
        return dateFormat;
    }
}
