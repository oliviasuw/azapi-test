/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes.car;

import agents.Agent;
import attributes.Behavior;
import data.CarData;
import java.util.ArrayDeque;
import services.ClockService;
import services.RoadService;

/**
 *
 * @author Eran
 */
public class ChangeRoad extends Behavior {

    private final CarData carData;
    private final RoadService roadService;
    private final ClockService clockService;

    public ChangeRoad(Agent a) {
        super(a);
        try {
            this.carData = a.getData(CarData.class);
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
        } else {
            if (canEnterToNextSegment(from, path)) { //can enter the next segment
                debug("\nentering new segment... " + path.size() + " more to go!\n");
                
                if (!from.equals(this.carData.getSource())) { //didn't just started driving (i.e, been in a previous segment)
//                    System.out.println("remove");
                    roadService.exitSegment(this.id);
                }
                
//                System.out.println("enter (" + from + " , " + path.peek() + ")");
                roadService.enterSegment(this.id, from, path.peek());
                this.carData.setCurrEdge(from, path.peek());
            }
            path.push(from);
        }
    }

    private boolean canEnterToNextSegment(String from, ArrayDeque<String> path) {
        return roadService.segmentNotFull(from, path.peek());
    }
}
