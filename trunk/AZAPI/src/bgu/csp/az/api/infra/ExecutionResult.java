/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

import bgu.csp.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public class ExecutionResult {

    private Assignment finalAssignment = null;
    private Exception crush = null;

    
    /**
     * indicate no solution result
     */
    public ExecutionResult(){
        
    }
    
    /**
     * indicate solution result
     * @param finalAssignment 
     */
    public ExecutionResult(Assignment finalAssignment) {
        this.finalAssignment = finalAssignment;
    }

    
    /**
     * indicate no solution because of a crush result
     * @param crush 
     */
    public ExecutionResult(Exception crush) {
        this.crush = crush;
    }

    public void setFinalAssignment(Assignment finalAssignment) {
        this.finalAssignment = finalAssignment;
    }

    public Assignment getFinalAssignment() {
        return finalAssignment;
    }

    public boolean hasSolution() {
        return this.finalAssignment != null;
    }
    
    public boolean isExecutionCrushed(){
        return crush != null;
    }
    
    public Exception getCrushReason(){
        return crush;
    }

}
