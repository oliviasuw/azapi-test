/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class VisualizationBuffer {
    private int pos;
    private ArrayList statesBuffer = new ArrayList(1000);
    
    
    /**
     * @return the next state object can also return null if no new state was recorded
     */
    public Object nextState(){
        if (statesBuffer.isEmpty() || statesBuffer.size() <= pos) return null;
        return statesBuffer.get(pos++);
    }
    
    public void buffer(Object state){
        this.statesBuffer.add(state);
    }
    
    public void buffer(List states){
        this.statesBuffer.addAll(states);
    }
    
    public int numberOfFrames(){
        return statesBuffer.size();
    }
    
    public void setBufferPosition(int pos){
        this.pos = pos;
    }
    
    public int getBufferPosition(){
        return this.pos;
    }
    
}
