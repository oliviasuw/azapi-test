/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

import bgu.csp.az.api.infra.stat.StatisticAnalyzer;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.api.tools.Assignment;

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

    /**
     * @return the name of this round
     */
    String name();

    /**
     * @return the length of the round - means how many executions should run under this round configuratios
     */
    int length();

    /**
     * the round seed - if there are any random elements on the round 
     * then this seed will give the ability to recreate the same round - if the seed is -1 
     * or not supplied (-1 is the default) then the seed will be the current time in miliseconds
     * @return the round seed 
     */
    long seed();

    /**
     * @return this round problem generator
     */
    ProblemGenerator getProblemGenerator();
    
    /**
     * register statistic analayzer to this round
     * @param analyzer 
     */
    void registerStatisticAnalyzer(StatisticAnalyzer analyzer);

    /**
     * @return list of all the registered statistics analayzers
     */
    StatisticAnalyzer[] getRegisteredStatisticAnalayzers();
    
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
    }
}
