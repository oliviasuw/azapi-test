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
class ActivityStatisticsCollector {
    
    private HashSet<Integer> people;
    private LinkedList<Integer> samples;

    public ActivityStatisticsCollector() {
        samples = new LinkedList<>();
    }
    
    public void start(int agentID){
        people.add(agentID);
    }
    
    public void end(int agentID){
        people.remove(agentID);
    }
    
    public void tick(){
        samples.add(people.size());
    }

    Iterable<Integer> getSamples() {
        return this.samples;
    }
    
}
