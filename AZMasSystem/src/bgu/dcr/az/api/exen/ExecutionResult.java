/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.mas.cp.ExecutionSelector;

/**
 * TODO: hide all the to* so that correctness testers will not have the power to
 * affect the result directly
 *
 * @author bennyl
 */
public class ExecutionResult<SOLUTION_TYPE extends DeepCopyable> implements DeepCopyable {

    private SOLUTION_TYPE finalSolution = null;
    private SOLUTION_TYPE correctSolution = null;
    private Exception crushReason = null;
    private State state = State.SUCCESS;
    private ExecutionSelector lastRunExecution;

    @Override
    public String toString() {
        return state.toString(this);
    }
    
    public State getState() {
        return state;
    }

    public ExecutionSelector getLastRunExecution() {
        return lastRunExecution;
    }

    public ExecutionResult<SOLUTION_TYPE> setLastRunExecution(ExecutionSelector lastRunExecution) {
        this.lastRunExecution = lastRunExecution;
        return this;
    }

    public SOLUTION_TYPE getCorrectSolution() {
        return correctSolution;
    }

    public ExecutionResult<SOLUTION_TYPE> toSucceefulState(SOLUTION_TYPE finalSolution) {
        this.finalSolution = finalSolution;
        this.state = State.SUCCESS;
        return this;
    }

    /**
     * indicate that the execution was ended with timeout
     *
     * @return
     */
    public ExecutionResult toEndedByLimiterState() {
        this.state = State.LIMITED;
        return this;
    }

    public ExecutionResult toCrushState(Exception reason) {
        crushReason = reason;
        this.state = State.CRUSHED;
        return this;
    }

    public ExecutionResult<SOLUTION_TYPE> toWrongState(SOLUTION_TYPE currectSolution) {
        this.correctSolution = currectSolution;
        this.state = State.WRONG;
        return this;
    }

    public SOLUTION_TYPE getSolution() {
        return finalSolution;
    }

    public Exception getCrushReason() {
        return crushReason;
    }

    @Override
    public ExecutionResult<SOLUTION_TYPE> deepCopy() {
        ExecutionResult<SOLUTION_TYPE> ret = new ExecutionResult<>();
        ret.state = this.state;
        ret.crushReason = this.crushReason;
        ret.finalSolution = (SOLUTION_TYPE) this.finalSolution.deepCopy();
        return ret;
    }

    public static enum State {

        WRONG {

                    @Override
                    public String toString(ExecutionResult er) {
                        return "This Execution ended with wrong results: it result was " + er.finalSolution + " while example of a correct result is: " + er.correctSolution;
                    }
                },
        CRUSHED {

                    @Override
                    public String toString(ExecutionResult er) {
                        return "The Execution crushed with the exception: " + (er.crushReason != null ? er.crushReason.getMessage() : "no-exception");
                    }
                },
        LIMITED {

                    @Override
                    public String toString(ExecutionResult er) {
                        return "The Execution was limited by the attached limiter " + (er.crushReason != null ? er.crushReason.getMessage() : "no-exception");
                    }
                },
        SUCCESS {

                    @Override
                    public String toString(ExecutionResult er) {
                        return "The Execution was ended successfully with the final assignment: " + er.finalSolution;
                    }
                };

        public abstract String toString(ExecutionResult er);
    }
}
