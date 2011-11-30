/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public class ExecutionResult implements DeepCopyable{

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

    public Assignment getAssignment() {
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

    @Override
    public ExecutionResult deepCopy() {
        ExecutionResult ret = new ExecutionResult();
        ret.crush = this.crush;
        ret.finalAssignment = (this.finalAssignment == null? null: this.finalAssignment.copy());
        return ret;
    }

}
