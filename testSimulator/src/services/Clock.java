/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import statistics.Utility;

/**
 *
 * @author Eran
 */
public class Clock {

    /**
     * time[0] = hours, time[1] = minutes, time[2] = seconds
     */
    private int[] time;
    public static final int[] cicle = {24, 60, 60};

    private static final int tickConversion = Utility.tickConversion;

    public Clock() {
        this.time = new int[]{0, 0, 0};
    }

    /**
     * assuming the input will always be a prefix of [hour, minute, second].
     *
     * @param time
     */
    public Clock(int... time) {
        this.time = new int[]{0, 0, 0};
        System.arraycopy(time, 0, this.time, 0, time.length);
    }

    public void tick() {
        int carry = 1;
        for (int i = Clock.tickConversion; i >= 0; i--) {
            if (carry == 0) {
                return;
            }
            carry = (time[i] + 1) / cicle[i];
            time[i] = (time[i] + 1) % cicle[i];
        }
    }

    public void reset() {
        this.time[0] = this.time[1] = this.time[2] = 0;
    }

    public int getTicks() {
        switch (Clock.tickConversion) {
            case 0: // tick is interpreted as an hour.
                return time[0];
            case 1: // tick is interpreted as an minute.
                return time[0] * 60 + time[1];
            case 2: // tick is interpreted as an second.
                return (time[0] * 60 + time[1]) * 60 + time[2];
            default:
                throw new UnsupportedOperationException("Unknown value for tick-conversion.");
        }
    }

    /**
     * Compare the time in the clocks. Return 0 if equal, 1 if current clock
     * preceed the other one and -1 if the other clock preceed the current one.
     *
     * @param other
     * @return
     */
    public int compare(Clock other) {
        for (int i = 0; i < time.length; i++) {
            if (time[i] != other.time[i]) {
                return (int) Math.signum(time[i] - other.time[i]);
            }
        }
        return 0;
    }

    /**
     * Compare the time [h:m:s] to the time in the current clock. Return 0 if
     * equal, 1 if current clock preceed the other one and -1 if the other clock
     * preceed the current one.
     *
     * @param h
     * @param m
     * @param s
     * @return
     */
    public int compare(int h, int m, int s) {
        int[] d = {time[0] - h, time[1] - m, time[2] - s};
        for (int i = 0; i < d.length; i++) {
            if (d[i] != 0) {
                return (int) Math.signum(d[i]);
            }
        }
        return 0;
    }

    public void set(int... time) {
        int toCopy = Math.min(time.length, Clock.tickConversion + 1);
        System.arraycopy(time, 0, this.time, 0, toCopy);
    }

    /**
     * Set the time to be some random value in the range (minH,minM,minS) -
     * (maxH,maxM,maxS).
     *
     * @param minH
     * @param minM
     * @param minS
     * @param maxH
     * @param maxM
     * @param maxS
     */
    public void setFromRange(int minH, int minM, int minS, int maxH, int maxM, int maxS) {
        int[] randInterval = Utility.subClocks(new int[]{minH, minM, minS}, new int[]{maxH, maxM, maxS});
        int randH = Utility.rand.nextInt(randInterval[0]);
        int randM = Utility.rand.nextInt(randInterval[1]);
        int randS = Utility.rand.nextInt(randInterval[2]);
        set(randH, randM, randS);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", time[0], time[1], time[2]);
    }

    /**
     * Input should be in the pattern: 'hh:mm:ss'.
     * @param str
     * @return 
     */
    public static int[] parseStringClock(String str) {
        int h = Integer.parseInt(str.substring(0, 2));
        int m = Integer.parseInt(str.substring(3, 5));
        int s = Integer.parseInt(str.substring(6, 8));
        return new int[]{h,m,s};
    }

}
