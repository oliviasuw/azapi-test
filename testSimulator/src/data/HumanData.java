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
public class HumanData extends Data{
    
    private final String homeAddr;
    private final Clock sleepHour;
    
    public Clock totalSpendTime;
    public Clock timeSpent;
    
    
    public HumanData(String home, Clock leavingHour) {
        this.homeAddr = home;
        this.sleepHour = Utility.generateStartSleepingHour(leavingHour);
        
        this.timeSpent = new Clock();
        this.totalSpendTime = new Clock();
        Utility.generateSpendingTime(this.totalSpendTime);
    }

    public Clock getSleepHour() {
        return sleepHour;
    }
    
    public String getHomeAddr(){
        return homeAddr;
    }

    public void refreshSpendTime() {
        Utility.generateSpendingTime(this.totalSpendTime);
    }
}
