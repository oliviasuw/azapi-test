/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package agentData;

import services.Clock;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class WorkerData extends Data{
    
    private final String work;
    private final Clock workingHours, leavingHour; //when to go to work.
    private Clock hourDone;
    
    
    public WorkerData(String work, Clock[] dailySchedule) {
        
        this.leavingHour = dailySchedule[0];
        this.workingHours = dailySchedule[1];
        
        this.work = work;
        this.hourDone = new Clock();
    }

    public Clock getLeavingHour() {
        return leavingHour;
    }

    public Clock getWorkingHours() {
        return workingHours;
    }

    public Clock getHoursDone() {
        return hourDone;
    }

    public void resetHoursDone() {
        this.hourDone.reset();
    }
    
    public void incrHoursDone(){
        this.hourDone.tick();
    }

    public String getWorkAddr() {
        return work;
    }
}
