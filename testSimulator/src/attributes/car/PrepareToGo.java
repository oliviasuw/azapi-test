/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes.car;

import agentData.HumanData;
import agentData.WorkerData;
import agentData.CarData;
import agents.Agent;
import agents.Employee;
import attributes.Behavior;
import services.Clock;
import services.ClockService;
import services.RoadService;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class PrepareToGo extends Behavior {

    private final WorkerData workerData;
    private final CarData carData;
    private final HumanData humanData;
    private final RoadService roadService;
    private final ClockService clockService;

    public PrepareToGo(Agent a) {
        super(a);
        try {
            if (a.getClass() == Employee.class) {
                this.workerData = a.getData(WorkerData.class);
            } else {
                this.workerData = null;
            }
            this.carData = a.getData(CarData.class);
            this.humanData = a.getData(HumanData.class);
            this.roadService = a.getService(RoadService.class);
            this.clockService = a.getService(ClockService.class);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Can't instantiate the behavior " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Override
    public void behave(String currState) {
        String tmp, source = humanData.getHomeAddr(), destination = "";
        boolean travel = false, working = false;
        
        if (canTravel()) {
            String hs;
            for (int i = 0; i < 4; i++) {
                hs = roadService.getRandomHotSpot();
                
                if ((tmp = this.roadService.findClosePL(id, source, hs)) != null) {
                    travel = true;
                    
                    this.carData.setDrivingDirection(CarData.Direction.Spend);
                    this.humanData.refreshSpendTime();
                    destination = tmp;
                    this.carData.setParkingAtPL(true);
                    debug(String.format("prepare to go to spend some time ... %s --> %s", source, destination));
                    break;
                }
            }
        }

        if (shouldGoToWork()) {
            working = true;
            debug(String.format("prepare to go to work ... %s --> %s", source, destination));
            this.carData.setDrivingDirection(CarData.Direction.Work);
            destination = this.workerData.getWorkAddr();
            
            if ((tmp = this.roadService.findClosePL(id, source, destination)) != null) {
                destination = tmp;
                carData.setParkingAtPL(true);
            }
        }
        
        if(!travel && !working)
            return;

        this.carData.currPath = roadService.getPath(source, destination);
        this.carData.setSource(source);
        this.carData.setDestination(destination);
    }

    /**
     * Check if an agent is allowed to go and spend some free time (shopping,
     * eating...).
     *
     * @return
     */
    private boolean canTravel() {
        Clock sleepTime = this.humanData.getSleepHour();
        Clock exitToWorkTime = (this.workerData == null) ? null : this.workerData.getLeavingHour();

        boolean shouldntGoSleep = Utility.notInSleepingInterval(sleepTime.toString(), clockService.toString());
        boolean alreadyWorked = (exitToWorkTime == null) ? true : clockService.compare(exitToWorkTime) > 0;

        return shouldntGoSleep && alreadyWorked;
    }

    /**
     * Check if it's time for a worker to go to work.
     * @return 
     */
    private boolean shouldGoToWork() {
        if (this.workerData == null) {
            return false;
        }
        boolean timeToGo = this.clockService.compare(this.workerData.getLeavingHour()) == 0;
        boolean sick = false;
        if(timeToGo)
            sick = Utility.rand.nextDouble() < Utility.PROB_SICK;
        return timeToGo && !sick;
    }

}
