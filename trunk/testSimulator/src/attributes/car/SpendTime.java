/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes.car;

import agents.Agent;
import attributes.Behavior;
import static attributes.Behavior.debug;
import data.CarData;
import data.HumanData;
import services.Clock;
import services.ClockService;
import services.RoadService;

/**
 *
 * @author Eran
 */
public class SpendTime extends Behavior {

    private final CarData carData;
    private final HumanData humanData;
    private final RoadService roadService;
    private final ClockService clockService;

    public SpendTime(Agent a) {
        super(a);
        try {
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
        Clock spentHours = humanData.timeSpent;
        if(humanData.totalSpendTime.compare(spentHours) == 0)
            spentHours.reset();
        debug("having fun ...");
        spentHours.tick();
        
        if(humanData.totalSpendTime.compare(spentHours) == 0){    
            debug("Done having fun!");
            this.carData.currPath = roadService.getPath(this.carData.getDestination(), this.humanData.getHomeAddr());
            debug("preparing to go home ...");
            
            this.carData.setDrivingDirection(CarData.Direction.Home);
            if(this.carData.isParkingAtPL())
                this.roadService.exitFromPL(id, this.carData.getDestination());
            this.carData.setParkingAtPL(false);
            
            this.carData.setSource(this.carData.getDestination());
            this.carData.setDestination(this.humanData.getHomeAddr());
        }
    }
}
