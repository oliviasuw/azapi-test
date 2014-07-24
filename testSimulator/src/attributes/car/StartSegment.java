/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package attributes.car;

import agents.Agent;
import attributes.Behavior;
import data.CarData;
import java.util.HashMap;
import services.RoadService;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class StartSegment extends Behavior {
    private final CarData carData;
    private final RoadService roadService;

    public StartSegment(Agent a) {
        super(a);
        
        try{
            this.carData = a.getData(CarData.class);
//            this.carData.setCurrSegmentLength(0);
            this.roadService = a.getService(RoadService.class);
        } catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException("Can't instantiate the behavior " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    

    @Override
    public void behave(String currState) {
        if(this.carData.getCurrSegmentLength() == 0){ //just started travel this segment
            debug("starting new segment!");
            String segmentStart = this.carData.currPath.pop(), segmentEnd = this.carData.currPath.peek();
            
            this.carData.setCurrSegmentLength(this.roadService.getEdgeLength(segmentStart, segmentEnd)); //update length of current segment
            setSpeed();
        }
    }

    /**
     * adjust the car's speed relying on the length of the current road-segment.
     */
    private void setSpeed() {
        String src = this.carData.getCurrSource(), dst = this.carData.getCurrDestination();
        HashMap<String, String> edgeAttr = this.roadService.getEdgeAttributes(src, dst);
        double speed = Utility.calculateCarSpeed(edgeAttr, this.carData.getCurrSegmentLength());
        this.carData.setSpeed(speed);
    }
}
