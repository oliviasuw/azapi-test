/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.pgen.ProblemGenerator;
import bgu.dcr.az.api.tools.Assignment;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * round is a configureable execution - it is part of the expirement and it define 
 * the execution of the collection of problems + algorithms it contains
 * the round also contains the means to analyze the statistics that gathered during it execution
 * @author bennyl
 * 
 * TODO: MISSING FUNCTIONS FOR ADD AND REMOVE ALGORITHM METADATA (NEDD TO API THE ALGORITHM METADATA)
 * 
 */
public interface Round extends Configureable, Process {

    void addListener(RoundListener l);
    
    void removeListener(RoundListener l);
    
    int getLength();
    
    /**
     * @return the name of this round
     */
    String getName();

    /**
     * the round seed - if there are any random elements on the round 
     * then this seed will give the ability to recreate the same round - if the seed is -1 
     * or not supplied (-1 is the default) then the seed will be the current time in miliseconds
     * @return the round seed 
     */
    long getSeed();
    String getRunningVarName();
    float getVarStart();
    float getVarEnd();
    float getTick();
    int getTickSize();
    float getCurrentVarValue();

    /**
     * @return return the round progress - when the round is running this function 
     * will return the current execution number between 0 and Round.getLength()
     */
    int getCurrentExecutionNumber();
    
    
    /**
     * @return this round problem generator
     */
    ProblemGenerator getProblemGenerator();
    
    /**
     * register statistic analayzer to this round
     * @param analyzer 
     */
    void registerStatisticCollector(StatisticCollector analyzer);

    /**
     * @return list of all the registered statistics analayzers
     */
    StatisticCollector[] getRegisteredStatisticCollectors();
    
    /**
     * return the round result after execution
     */
    RoundResult getResult();
    
    CorrectnessTester getCorrectnessTester();
    
    void setCorrectnessTester(CorrectnessTester ctester);
    
    public static enum FinishStatus{
        SUCCESS,
        WRONG_RESULT,
        CRUSH;
    }
    
    public static class RoundResult{
        public final FinishStatus finishStatus;
        public final Exception crushReason;
        public final Assignment goodAssignment;
        public final Execution badExecution;

        /**
         * constract successfull result
         */
        public RoundResult() {
            this.finishStatus = FinishStatus.SUCCESS;
            this.crushReason = null;
            this.goodAssignment = null;
            this.badExecution = null;
        }
        
        /**
         * constract crushed result
         * @param exception
         * @param badExecution 
         */
        public RoundResult(Exception exception, Execution badExecution) {
            this.finishStatus = FinishStatus.CRUSH;
            this.crushReason = exception;
            this.goodAssignment = null;
            this.badExecution = badExecution;
        }
        
        /**
         * constract wrong result
         * @param goodAssignment
         * @param badExecution 
         */
        public RoundResult(Assignment goodAssignment, Execution badExecution) {
            this.finishStatus = FinishStatus.WRONG_RESULT;
            this.crushReason = null;
            this.goodAssignment = goodAssignment;
            this.badExecution = badExecution;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("round result:\n").append("status= ").append(finishStatus);
            switch (finishStatus){
                case CRUSH:
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    crushReason.printStackTrace(pw);
                    sb.append("crushReason: ").append(sw.toString());
                    break;
                case WRONG_RESULT:
                    sb.append("\nwrong assignment: ")
                            .append("" + badExecution.getResult().getAssignment()).append(" with cost of = ").append(badExecution.getResult().getAssignment().calcCost(badExecution.getGlobalProblem()))
                            .append(" while good assignment is: ").append(goodAssignment.toString()).append(" with cost of = ").append(goodAssignment.calcCost(badExecution.getGlobalProblem()));
                    break;
            }
            return sb.toString();
            
            
        }
        
    }
    
    public static interface RoundListener{
        void onRoundStarted(Round source);
        void onExecutionStarted(Round source, Execution exec);
        void onExecutionEnded(Round source, Execution exec);
    }
}
