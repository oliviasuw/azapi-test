/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes.car;

import agents.Agent;
import attributes.Behavior;
import agentData.CarData;
import agentData.TankData;
import data.events.impl.ParkingEvent;
import java.util.ArrayDeque;
import services.ClockService;
import services.RoadService;
import testsimulator.TestSimulator;

/**
 *
 * @author Eran
 */
public class ChangeRoad extends Behavior {

    private final CarData carData;
    private final TankData tankData;
    private final RoadService roadService;
    private final ClockService clockService;

    public ChangeRoad(Agent a) {
        super(a);
        try {
            this.carData = a.getData(CarData.class);
            this.tankData = a.getData(TankData.class);
            this.roadService = a.getService(RoadService.class);
            this.clockService = a.getService(ClockService.class);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Can't instantiate the behavior " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Override
    public void behave(String currState) {
        ArrayDeque<String> path = this.carData.currPath;
        String from = path.pop();

        if (from.equals(this.carData.getDestination())) { //reached path destination (either workplace or home)
            debug("Reached destination. ");
//            System.out.println("exit");
            roadService.exitSegment(this.id);
            
            if(this.carData.isParkingAtPL()){
                double percentage = tankData.getCurrAmount() / tankData.getCapacity();
                ParkingEvent.CarType carType = (tankData.isElectric())? ParkingEvent.CarType.ELECTRIC: ParkingEvent.CarType.FUEL;
                String pl = this.roadService.getAssociatedPL(this.carData.getDestination());
                if(pl == null || carType == null )
                    System.out.println("HERE!!");
                ParkingEvent parkEvent = new ParkingEvent(id, pl, percentage,ParkingEvent.InOut.IN, carType);
                TestSimulator.eventWriter.writeEvent(TestSimulator.output, parkEvent);
//                System.out.println(String.format("Sending ParkEvent:: Enter (%d, %s, %f, %s, %s)"));
            }
        } else {
            boolean emergencyFound = false;
            if (tankData.isCritical() && carData.getDrivingDirection() != CarData.Direction.Emergency) {                
                emergencyFound = goRecharge(from);
            }
            if (!emergencyFound && canEnterToNextSegment(from, path)) { //can enter the next segment
                debug("\nentering new segment... " + path.size() + " more to go!\n");

                if (!from.equals(this.carData.getSource())) { //didn't just started driving (i.e, been in a previous segment)
                    roadService.exitSegment(this.id);
                }

                roadService.enterSegment(this.id, from, path.peek());
                this.carData.setCurrEdge(from, path.peek());
            }
            path.push(from);
        }
    }

    private boolean canEnterToNextSegment(String from, ArrayDeque<String> path) {
        return roadService.segmentNotFull(from, path.peek());
    }

    /**
     * Need to recharge now, re-route for a close parking-lot/fuel-station.
     * @param newSRC 
     */
    private boolean goRecharge(String newSRC) {
        String dest;
        if(this.tankData.isElectric())
            dest = this.roadService.findClosePL(id, newSRC, newSRC);
        else
            dest = this.roadService.findCloseFS(id, newSRC, newSRC);
        
        if (dest != null) {
            this.carData.cacheData();
            
            this.carData.setDrivingDirection(CarData.Direction.Emergency);
            
            if(this.tankData.isElectric())
                this.carData.setParkingAtPL(true);
            System.out.println("going recharge NOW ...");
        
            this.carData.currPath = roadService.getPath(newSRC, dest);
            this.carData.setSource(newSRC);
            this.carData.setDestination(dest);
            return true;
        }
        else
            return false;
        
    }
}
