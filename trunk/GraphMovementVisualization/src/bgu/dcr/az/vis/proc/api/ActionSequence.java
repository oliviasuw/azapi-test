/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

/**
 *
 * @author Zovadi
 */
public interface ActionSequence {

    ActionSequence addAction(Action action);
    
    ActionSequence addActionSequence(ActionSequence sequence);
    
    /**
     * 
     * @return the duration fraction for the next action in this sequence
     * or null if no actions exists
     */
    Double getNextPOI();
    
    /**
     * advances the sequence to the state after given amount of time
     * @param duration
     * @return 
     */
    ActionSequence advance(double duration);
    
    Action getFirstAction();
}
