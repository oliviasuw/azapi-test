/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import attributes.Behavior;
import attributes.State;
import attributes.car.ChangeRoad;
import attributes.car.PrepareToGo;
import attributes.car.DriveSegment;
import attributes.car.StartSegment;
import attributes.car.DoWork;
import attributes.car.SpendTime;
import data.CarData;
import data.Data;
import data.HumanData;
import data.TankData;
import data.WorkerData;
import java.util.HashMap;
import services.Clock;
import services.ClockService;
import services.RoadService;
import services.Service;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class Employee extends Agent {

    public Employee(HashMap<Class<? extends Service>, Service> s, HashMap<Class<? extends Data>, Data> d) {
        super(s, d);

        this.services.put(ClockService.class, s.get(ClockService.class));
        this.services.put(RoadService.class, s.get(RoadService.class));
        RoadService r = getService(RoadService.class);
        String[] home_work = r.generateWorkerInfo();
        Clock[] dailySchedule = Utility.generateDailySchedule();

        if (d.get(TankData.class) != null) {
            // need to add a copy constructor.. for now, the user is not allowed
            // to pre-define the agent's attributes.
            // this.dataMap.put(CarData.class, d.get(CarData.class));
            this.dataMap.put(TankData.class, new TankData(true));
        } else {
            this.dataMap.put(TankData.class, new TankData(true));
        }
        
        if (d.get(CarData.class) != null) {
            // need to add a copy constructor.. for now, the user is not allowed
            // to pre-define the agent's attributes.
            // this.dataMap.put(CarData.class, d.get(CarData.class));
            this.dataMap.put(CarData.class, new CarData());
        } else {
            this.dataMap.put(CarData.class, new CarData());
        }
        
        if (d.get(WorkerData.class) != null) {
            // need to add a copy constructor.. for now, the user is not allowed
            // to pre-define the agent's attributes.
            // this.dataMap.put(WorkerData.class, d.get(WorkerData.class));
            this.dataMap.put(WorkerData.class, new WorkerData(home_work[1], dailySchedule));
        } else {
            this.dataMap.put(WorkerData.class, new WorkerData(home_work[1], dailySchedule));
        }

        if (d.get(HumanData.class) != null) {
            // need to add a copy constructor.. for now, the user is not allowed
            // to pre-define the agent's attributes.
            // this.dataMap.put(HumanData.class, d.get(HumanData.class));
            this.dataMap.put(HumanData.class, new HumanData(home_work[0], dailySchedule[0]));
        } else {
            this.dataMap.put(HumanData.class, new HumanData(home_work[0], dailySchedule[0]));
        }
    }

    @Override
    public void init() {
        this.currState = "home";

        //states creation
        State change = new State("changeRoad");
        State home = new State("home");
        State drive = new State("drive");
        State work = new State("work");
        State spendTime = new State("spendTime");

        //add behaviors to each state
        change.add(new ChangeRoad(this));
        home.add(new PrepareToGo(this));
        work.add(new DoWork(this));
        drive.add(new StartSegment(this), new DriveSegment(this));
        spendTime.add(new SpendTime(this));

        //update the states-table with the newly created ones.
        addStates(change, home, drive, work, spendTime);
    }

    @Override
    protected void changeState() {
        RoadService rService;
        ClockService clock;
        WorkerData wData;
        CarData cData;
        HumanData hData;

        switch (currState) {
            case ("home"):
                clock = getService(ClockService.class);
//                wData = getData(WorkerData.class);
                cData = getData(CarData.class);
//                hData = getData(HumanData.class);
                if(cData.getDrivingDirection() != CarData.Direction.None){
//                if(cService.compare(wData.getLeavingHour()) == 0){
                    currState = "changeRoad";
                }
                break;

            case ("work"):
                wData = getData(WorkerData.class);
                if (wData.getHoursDone().compare(wData.getWorkingHours()) == 0) {
                    currState = "changeRoad";
                }
                break;

            case ("spendTime"):
                hData = getData(HumanData.class);
                if (hData.timeSpent.compare(hData.totalSpendTime) == 0) {
                    currState = "changeRoad";
                }
                break;

            case ("drive"):

                rService = getService(RoadService.class);
                cData = getData(CarData.class);

                if (rService.getPercantage(id) == 100) { //reached end of the segment.
                    Behavior.debug("segment's done!");
                    cData.setCurrSegmentLength(0);

                    if ((cData.currPath.size() >= 2) && ableToKeepDriving(cData, rService)) {
                        this.states.get("changeRoad").applyBehaviors();
                    } else {
                        currState = "changeRoad";
                    }
                }

                break;

            case ("changeRoad"):
                rService = getService(RoadService.class);
                cData = getData(CarData.class);

                if (cData.currPath.isEmpty()) //reached destination
                {
                    currState = concludeNextState();
                    System.out.println("reached destination, next:" + currState);
                } else if (rService.getPercantage(id) == 0) { //managed to enter new segment
                    currState = "drive";
                }
                break;

            default:
                throw new UnsupportedOperationException(String.format("undefined state - %s", currState));
        }
    }

    /**
     * Conclude the next state, assuming the agent just reached his destination.
     *
     * @return
     */
    private String concludeNextState() {
        CarData cData = getData(CarData.class);
        CarData.Direction dir = cData.getDrivingDirection();
        
        cData.setDrivingDirection(CarData.Direction.None);

        switch(dir){
            case Home: return "home";
            case Work: return "work";
            case Spend: return "spendTime";
            default: throw new UnsupportedOperationException("Where the hell are you driving to??");
        }
    }

    /**
     * Check whether the car can keep driving (i.e, automatically pass the
     * junction, no need to pay attention to other edges). The next segment to
     * drive is (nextSRC, nextTRGT).
     *
     * @param cData
     * @param rService
     * @return
     */
    private boolean ableToKeepDriving(CarData cData, RoadService rService) {
        ClockService clock = getService(ClockService.class);

        String currSRC = cData.getCurrSource(), currTRGT = cData.getCurrDestination();
        String nextSRC = cData.currPath.pop(), nextTRGT = cData.currPath.peekFirst();
        cData.currPath.push(nextSRC);

        boolean onlyRoad = rService.isOnlyIncomingRoad(nextSRC, nextTRGT);
        boolean onlyOneWaiting = rService.waitingToEnterJunction(nextSRC) == 1;

        return (onlyRoad || onlyOneWaiting) && rService.greenLight(currSRC, currTRGT, clock.getTicks());
    }

    private void addStates(State... states) {
        for(State state : states)
            this.states.put(state.getName(), state);
    }

}
