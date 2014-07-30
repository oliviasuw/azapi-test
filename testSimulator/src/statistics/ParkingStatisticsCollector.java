/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statistics;

import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author Eran
 */
class ParkingStatisticsCollector {

    private HashSet<Integer> cars;
    private LinkedList<Integer> samples_utilization;

    public ParkingStatisticsCollector() {
        samples_utilization = new LinkedList<>();
    }
    
    public void enter(int agentID) {
        cars.add(agentID);
    }

    public void exit(int agentID) {
        cars.remove(agentID);
    }
    
    public void tick(){
        samples_utilization.add(cars.size());
    }

    public LinkedList<Integer> getSamples() {
        return samples_utilization;
    }
    
}
