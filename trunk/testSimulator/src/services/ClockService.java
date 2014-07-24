/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

/**
 *
 * @author Eran
 */
public class ClockService implements Service{
    
    private int ticks;
    private Clock clock;
    
    @Override
    public void init() {
        this.ticks = 0;
        this.clock = new Clock();
    }

    @Override
    public void tick() {
        this.ticks++;
        this.clock.tick();
    }
    
    /**
     * return the current ticks
     * @return 
     */
    public int getTicks(){
        return this.ticks;
    }
    
    /**
     * Checks if the time in the two clocks is equal.
     * @param other
     * @return 
     */
    public int compare(Clock other){
        return this.clock.compare(other);
    }
    
    /**
     * Checks if the time in the clocks is [h:m:s].
     * @param h
     * @param m
     * @param s
     * @return 
     */
    public int compare(int h, int m, int s){
        return this.clock.compare( h, m, s);
    }
    
    @Override
    public String toString(){
        return this.clock.toString();
    }
}
