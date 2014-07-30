/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package attributes.car;

import agents.Agent;
import attributes.Behavior;
import static attributes.Behavior.debug;
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
public class RefillTank extends Behavior {
    
    private final TankData tankData;
    private final CarData carData;
    private final RoadService roadService;
    
    public RefillTank(Agent a) {
        super(a);
        try{
            this.tankData = a.getData(TankData.class);
            this.carData = a.getData(CarData.class);
            this.roadService = a.getService(RoadService.class);
        } catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException("Can't instantiate the behavior " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Override
    public void behave(String currState) {
        debug((this.tankData.isElectric())? "charging .." : "filling tank ..");
        tankData.refill();
        
        if(tankData.isFull()){    
            debug("Tank full!");
            String currPos = carData.getDestination();
            
            this.carData.loadData();
            
            this.carData.currPath = roadService.getPath(currPos, this.carData.getDestination());
            if(!this.tankData.isElectric()){
                double percentage = tankData.getCurrAmount() / tankData.getCapacity();
                this.roadService.exitFromPL(id, currPos, percentage, tankData.isElectric());
            }
            this.carData.setSource(currPos);
            debug("Continue daily routine ..." + this.carData.getDrivingDirection());
        }
    }
    
}
