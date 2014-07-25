/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes;

import agents.Agent;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public abstract class Behavior {

    protected int id;

    public Behavior(Agent a) {
        this.id = a.getID();
    }

    /**
     * execute the behavior.
     *
     * @param currState
     */
    public abstract void behave(String currState);
    
    public static void debug(Object s){
        if(Utility.SIMULATOR_DEBUG)
            System.out.println(s);
    }
}
