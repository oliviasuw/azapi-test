/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package attributes.car;

import agents.Agent;
import attributes.Behavior;
import agentData.CarData;
import agentData.HumanData;
import agentData.TankData;
import agentData.WorkerData;
import services.Clock;
import services.RoadService;

/**
 *
 * @author Eran
 */
public class DoWork extends Behavior {
    
    private final HumanData humanData;
    private final WorkerData workerData;
    private final CarData carData;
    private final TankData tankData;
    private final RoadService roadService;
    
    public DoWork(Agent a) {
        super(a);
        try{
            this.tankData = a.getData(TankData.class);
            this.workerData = a.getData(WorkerData.class);
            this.humanData = a.getData(HumanData.class);
            this.carData = a.getData(CarData.class);
            this.roadService = a.getService(RoadService.class);
        } catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException("Can't instantiate the behavior " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    

    @Override
    public void behave(String currState) {
        Clock workingHours = workerData.getWorkingHours();
        if(workerData.getHoursDone().compare(workingHours) == 0)
            workerData.resetHoursDone();
        debug("working ...");
        workerData.incrHoursDone();
        
        if(workerData.getHoursDone().compare(workingHours) == 0){    
            debug("Done Working!");
            this.carData.currPath = roadService.getPath(this.carData.getDestination(), this.humanData.getHomeAddr());
            debug(String.format("preparing to go home ... %s --> %s",this.carData.getDestination(), this.humanData.getHomeAddr()));
            
            if(this.carData.isParkingAtPL()){
                double percentage = tankData.getCurrAmount() / tankData.getCapacity();
                this.roadService.exitFromPL(id, this.carData.getDestination(), percentage, tankData.isElectric());
            }
            this.carData.setParkingAtPL(false);
            
            this.carData.setSource(this.carData.getDestination());
            this.carData.setDestination(this.humanData.getHomeAddr());
            this.carData.setDrivingDirection(CarData.Direction.Home);
        }
    }
    
}
