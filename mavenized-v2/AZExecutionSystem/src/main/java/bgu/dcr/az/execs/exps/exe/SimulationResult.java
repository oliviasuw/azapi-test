/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.common.deepcopy.DeepCopyUtil;
import bgu.dcr.az.common.deepcopy.DeepCopyable;
import bgu.dcr.az.common.io.StringBuilderWriter;
import java.io.PrintWriter;

/**
 * TODO: hide all the to* so that correctness testers will not have the power to
 * affect the result directly
 *
 * @author bennyl
 */
public class SimulationResult<SOLUTION_TYPE> implements DeepCopyable {

    private SOLUTION_TYPE finalSolution = null;
    private SOLUTION_TYPE correctSolution = null;
    private Exception crushReason = null;
    private State state = State.SUCCESS;
    private int lastRunExecution;

    @Override
    public String toString() {
        return state.toString(this);
    }

    public State getState() {
        return state;
    }

    public int getLastRunExecution() {
        return lastRunExecution;
    }

    public SimulationResult<SOLUTION_TYPE> setLastRunExecution(int lastRunExecution) {
        this.lastRunExecution = lastRunExecution;
        return this;
    }

    public SOLUTION_TYPE getCorrectSolution() {
        return correctSolution;
    }

    public SimulationResult<SOLUTION_TYPE> toSucceefulState(SOLUTION_TYPE finalSolution) {
        this.finalSolution = finalSolution;
        this.state = State.SUCCESS;
        return this;
    }

    /**
     * indicate that the execution was ended with timeout
     *
     * @return
     */
    public SimulationResult toEndedByLimiterState() {
        this.state = State.LIMITED;
        return this;
    }

    public SimulationResult toCrushState(Exception reason) {
        crushReason = reason;
        this.state = State.CRUSHED;
        return this;
    }

    public SimulationResult<SOLUTION_TYPE> toWrongState(SOLUTION_TYPE currectSolution) {
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
    public SimulationResult<SOLUTION_TYPE> deepCopy() {
        SimulationResult<SOLUTION_TYPE> ret = new SimulationResult<>();
        ret.state = this.state;
        ret.crushReason = this.crushReason;
        ret.finalSolution = DeepCopyUtil.deepCopy(this.finalSolution);
        return ret;
    }

    public SimulationResult<SOLUTION_TYPE> toNotRunYetState() {
        this.state = State.NOT_RUN_YET;
        return this;
    }

    public static enum State {

        NOT_RUN_YET {
                    @Override
                    public String toString(SimulationResult er) {
                        return "The execution did not run yet.";
                    }
                },
        WRONG {

                    @Override
                    public String toString(SimulationResult er) {
                        return "This Execution ended with wrong results: it result was " + er.finalSolution + " while example of a correct result is: " + er.correctSolution;
                    }
                },
        CRUSHED {

                    @Override
                    public String toString(SimulationResult er) {
                        StringBuilderWriter w = new StringBuilderWriter(new StringBuilder());
                        if (er.crushReason != null) {
                            er.crushReason.printStackTrace(new PrintWriter(w));
                        }
                        return "The Execution crushed with the exception: " + (er.crushReason != null ? w.toString() : "no-exception");
                    }
                },
        LIMITED {

                    @Override
                    public String toString(SimulationResult er) {
                        return "The Execution was limited by the attached limiter " + (er.crushReason != null ? er.crushReason.getMessage() : "no-exception");
                    }
                },
        SUCCESS {

                    @Override
                    public String toString(SimulationResult er) {
                        return "The Execution was ended successfully with the final assignment: " + er.finalSolution;
                    }
                };

        public abstract String toString(SimulationResult er);
    }
}
