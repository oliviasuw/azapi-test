/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statistics;

import java.util.HashMap;
import javafx.scene.chart.XYChart;

/**
 *
 * @author Eran
 */
public class Statistics {
    public final int ACTIVITY_WORK = 0, ACTIVITY_FUN = 1, ACTIVITY_DRIVE = 2, ACTIVITY_HOME = 3, ACTIVITY_EMERGENCY = 4;
    private HashMap<String, ParkingStatisticsCollector> plCollector;
    private ActivityStatisticsCollector[] activityCollector;
    
    public Statistics(int agents, int ticks){
        plCollector = new HashMap<>();
        
        activityCollector = new ActivityStatisticsCollector[5];
        for (int i = 0; i < activityCollector.length; i++) {
            activityCollector[i] = new ActivityStatisticsCollector();
        }
    }
    
    public void endTick(){
        for (ParkingStatisticsCollector pl : plCollector.values()) {
            pl.tick();
        }
        
        for (ActivityStatisticsCollector activity : activityCollector){
            activity.tick();
        }
    }
    
    public void enterPL(int agentID, int hour, String pl, double currPower){
        this.plCollector.putIfAbsent(pl, new ParkingStatisticsCollector());
        this.plCollector.get(pl).enter(agentID);
    }
    
    public void exitPL(int agentID, int hour, String pl){
        this.plCollector.get(pl).exit(agentID);
    }
    
    public void startActivity(int activity, int agentID){
        activityCollector[activity].start(agentID);
    }
    
    public void endActivity(int activity, int agentID){
        activityCollector[activity].end(agentID);
    }
    
    public void finish(){
        for(ParkingStatisticsCollector pl : plCollector.values()){
            int c = 0;
            XYChart.Series plUtilization = new XYChart.Series();
            for(Integer sample : pl.getSamples())
                plUtilization.getData().add(new XYChart.Data(c++, sample));
        }
        
        for(ActivityStatisticsCollector activity : activityCollector){
            int c = 0;
            XYChart.Series plUtilization = new XYChart.Series();
            for(Integer sample : activity.getSamples())
                plUtilization.getData().add(new XYChart.Data(c++, sample));
        }
    }
}
