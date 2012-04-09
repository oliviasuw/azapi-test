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
import java.util.List;

/**
 * test is a configurable execution - it is part of the expirement and it define 
 * the execution of the collection of problems + algorithms it contains
 * the test also contains the means to analyze the statistics that gathered during it execution
 * @author bennyl
 * 
 * TODO: MISSING FUNCTIONS FOR ADD AND REMOVE ALGORITHM METADATA (NEDD TO API THE ALGORITHM METADATA)
 * 
 */
public interface Test extends Process {

    void addListener(TestListener l);
    
    void removeListener(TestListener l);
    
    /**
     * @return number of executions * number of algorithms in this test
     */
    int getLength();

    int getNumberOfExecutions();
    
    List<String> getIncludedAlgorithmsInstanceNames();
    
    /**
     * @return the name of this test
     */
    String getName();

    /**
     * the test seed - if there are any random elements on the test 
     * then this seed will give the ability to recreate the same test - if the seed is -1 
     * or not supplied (-1 is the default) then the seed will be the current time in miliseconds
     * @return the test seed 
     */
    long getSeed();
    /**
     * @return the variable name that this test is executing
     */
    String getRunningVarName();
    /**
     * @return the starting value of var
     */
    float getVarStart();
    /**
     * @return the ending value of var
     */
    float getVarEnd();
    /**
     * @return the amount to add to the run var after the repeat count
     */
    float getTickSize();
    /**
     * @return how many executions will be under each run var value
     */
    int getRepeatCount();
    
    /**
     * @return the current run var value
     */
    double getCurrentVarValue();

    /**
     * @return the test progress - when the test is running this function 
     * will return the current execution number between 0 and Test.getLength()
     */
    int getCurrentExecutionNumber();
        
    /**
     * @return this test problem generator
     */
    ProblemGenerator getProblemGenerator();
    
    /**
     * register statistic analayzer to this test
     * @param analyzer 
     */
    void addStatisticCollector(StatisticCollector analyzer);

    /**
     * @return list of all the registered statistics analayzers
     */
    List<StatisticCollector> getStatisticCollectors();
    
    /**
     * return statistic collector for the given type
     * @param type the class of the statistic collector
     * @return the statistic collector if it is loaded or null if it not.
     */
    <T extends StatisticCollector> T getStatisticCollector(Class<T> type);
    
    /**
     * return the test result after execution
     */
    TestResult getResult();
    
    CorrectnessTester getCorrectnessTester();
    
    int getCurrentProblemNumber();
    
    /**
     * you can set the correctness tester to null in order to remove it.
     * @param ctester 
     */
    void setCorrectnessTester(CorrectnessTester ctester);

    public String getCurrentExecutedAlgorithmInstanceName();
    
    public static enum FinishStatus{
        SUCCESS,
        WRONG_RESULT,
        CRUSH;
    }
    
    public static class TestResult{
        public final FinishStatus finishStatus;
        public final Exception crushReason;
        public final Assignment goodAssignment;
        public final Execution badExecution;

        /**
         * constract successfull result
         */
        public TestResult() {
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
        public TestResult(Exception exception, Execution badExecution) {
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
        public TestResult(Assignment goodAssignment, Execution badExecution) {
            this.finishStatus = FinishStatus.WRONG_RESULT;
            this.crushReason = null;
            this.goodAssignment = goodAssignment;
            this.badExecution = badExecution;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("test result:\n").append("status= ").append(finishStatus);
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
    
    public static interface TestListener{
        void onTestStarted(Test source);
        void onExecutionStarted(Test source, Execution exec);
        void onExecutionEnded(Test source, Execution exec);
    }
}
