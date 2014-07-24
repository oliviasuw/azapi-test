/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import java.util.HashMap;
import java.util.Random;
import services.Clock;

/**
 *
 * @author Eran
 */
public class Utility {

    /**
     * A random-variable that should be used by any class in the entire project
     * (if necessary).
     */
    public static final Random rand = new Random(17);

    /**
     * Holds the number of agents and ticks that will be simulated.
     */
    public static final int SIMULATOR_AGENTS = 1;
    public static final int SIMULATOR_TICKS = 6500;
    
    /**
     * Print debug messages along the simulator.
     */
    public static final boolean SIMULATOR_DEBUG = false;

    /**
     * Holds the length of a car (in order to avoid collisions. (UNINITIALIZED).
     */
    public static final double CAR_LENGTH = 0.001;

    /**
     * Supposed to hold the driving speed (??). Not used currently.
     */
    public static final double CAR_KMPH = 10;

    /**
     * The probability of a worker to be sick in a given day. (UNINITIALIZED).
     */
    public static final double PROB_SICK = 0.08;

    /**
     * The probability of a worker to be sick in a given day. (UNINITIALIZED).
     */
    public static final double PROB_EMPLOYMENT_RATIO = 1;

    /**
     * The time-slice of a traffic-light to be green. (UNINITIALIZED).
     */
    public static final int TL_INTERVAL = 20;

    /**
     * Used to assign random capacities (in a pre-defined range) to parking
     * lots. (UNINITIALIZED).
     */
    public static final int LOT_MIN_CAPACITY = 25;
    public static final int LOT_MAX_CAPACITY = 225;

    /**
     * For some vertex v, defines the radius (meters) of search for a parking
     * lot close to v.
     */
    public static final double LOT_SEARCH_RADIUS = 250;

    /**
     * Defines the accuracy of the tagging process of parking lots. Each vertex
     * within the targeting radius (meters) of some other vertex with the
     * "parking_lot" tag is considered a parking lot. Of course, the smaller the
     * radius, the more accurate it will become.
     */
    public static final double LOT_TAGGING_RADIUS = 7.5;
    
    /**
     * Sleeping hours of a man. (UNINITIALIZED).
     */
    private static final int[] sleepingHours = {5,0,0};

    /**
     * Each of the values 0,1,2 means that a tick is interpreted as a single
     * hour / minute / second respectively.
     */
    public static final int tickConversion = 1;
    
    public static double TANK_CRITICAL = 0.25;
    /**
     * Consumption ratios: fuel (liter/meters) and electricity (watt/meters).
     */
    public static double FUEL_CONSUMPTION_RATIO;
    public static double ELECTRICITY_CONSUMPTION_RATIO;
    public static double ELECTRICITY_RECHARGE_RATE;
    public static double FUEL_RECHARGE_RATE;

    /**
     * Calculate the car's speed on the current road-segment. (UNINITIALIZED)
     *
     * @param edgeAttr
     * @param length
     * @return
     */
    public static double calculateCarSpeed(HashMap<String, String> edgeAttr, double length) {
        double s;

//        if(length < Utility.KMPH)
//            s = 100;
//        else
//            s = (Utility.KMPH / length) * 100;
        s = 100;

        return s;
    }

    /**
     * Calculate the maximal capacity of a given road (not sure this is
     * necessary, maybe just generate random value from the given interval).
     * (UNINITIALIZED)
     *
     * @param edgeAttr
     * @return
     */
    public static int calculateSegmentCapacity(HashMap<String, String> edgeAttr) {
        return 1353543481;
    }

    /**
     * Calculate the traffic-light's time-slice to be green. Maybe can generate
     * random values within a range. (UNINITIALIZED).
     *
     * @return
     */
    public static int calculate_TLInterval() {
        return TL_INTERVAL;
    }

    /**
     * Calculate capacity for some parking lot.
     *
     * @return
     */
    public static int calculateLotCapacity() {
        return LOT_MIN_CAPACITY + rand.nextInt(LOT_MAX_CAPACITY - LOT_MIN_CAPACITY);
    }

    /**
     * Calculates a worker's daily schedule at work: when to leave home
     * (array[0]) and how many hours to work (array[1]). This is an alternative
     * solution to the method calculateWorkHours(). (UNINITIALIZED).
     *
     * @return
     */
    public static Clock[] generateDailySchedule() {
        Clock workHours = new Clock(0,5);
        Clock exitHour = new Clock(0);
        return new Clock[]{exitHour, workHours};
    }

    /**
     * Generate a sleeping hour for a man. If he is a worker
     * (leavingHour!=null), than his leavingHour need to be considered.
     * Otherwise, calculate by some other formula. (UNINITIALIZED).
     *
     * @param leavingHour
     * @return
     */
    public static Clock generateStartSleepingHour(Clock leavingHour) {
        return new Clock(23,15);
    }

    /**
     * Generate time for spending time (at a restaurant/shop/cinema, etc.).
     * (UNINITIALIZED). 
     * @param cl
     */
    public static void generateSpendingTime(Clock cl) {
        cl.set(1);
    }

    /**
     * Check that the current time is not in the middle of a sleeping process.
     * @param sleep
     * @param currTime
     * @return 
     */
    public static boolean notInSleepingInterval(String sleep, String currTime) {
        int[] start = Clock.parseStringClock(sleep);
        int[] curr = Clock.parseStringClock(currTime);
        int[] passed = subClocks(start, curr);        

        for (int i = 0; i < passed.length; i++) {
            if(sleepingHours[i] > passed[i]){
                return false;
            }
            if(sleepingHours[i] < passed[i])
                return true;
        }
        return true;
        
    }

    /**
     * Subtract two clocks (represented as arrays), and return the result.
     * @param start
     * @param curr
     * @return 
     */
    public static int[] subClocks(int[] start, int[] curr) {
        int c = 0;
        int[] sub = {0,0,0};
        for (int i = sub.length - 1; i >= 0; i--) {
            int d = curr[i] - c - start[i];
            sub[i] = (d < 0)? d + Clock.cicle[i] : d;
            c = (d < 0)? 1 : 0;
        }
        return sub;
    }

    public static double generateCapacity(boolean electric) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
