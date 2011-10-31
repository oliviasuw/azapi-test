/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.infra.EventPipe;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public abstract class Expirament extends ProcessImpl {

    private Thread executionThread;
    private AbstractExecution currentExecution;

    protected abstract AbstractExecution nextExecution();

    protected abstract boolean hasMoreExecutions();

    @Override
    public void stop() {
        System.out.println("Stopping Execution!");
        if (executionThread != null) {
            executionThread.interrupt();
        }
    }

    protected AbstractExecution getCurrentExecution() {
        return currentExecution;
    }

    @Override
    public void _run() {
        executionThread = Thread.currentThread();
        Problem p = null;
        EventPipe currentExecutionEventPipe;
        ExecutionResult correct;
        ExecutionResult result;
        double goodc;
        double gotc;
        try {
            while (hasMoreExecutions() && !Thread.currentThread().isInterrupted()) {
                currentExecution = nextExecution();
                currentExecutionEventPipe = new EventPipe();
                currentExecution.setEventPipe(currentExecutionEventPipe);
                p = currentExecution.getGlobalProblem();

                currentExecution.run();

                result = currentExecution.getResult();
                
                //CHECK CRUSH
                if (result.isExecutionCrushed()){
                    whenExpirementEndedBecauseExecutionCrushed(result.getCrushReason());
                    return;
                }
                
                //CHECK CORRECTNESS
                System.out.println("Testing solution...");
                correct = safeSolve(currentExecution.getGlobalProblem());
                if (correct != null) {
                    if (result.hasSolution() == true && correct.hasSolution() == true) {
                        goodc = correct.getAssignment().calcCost(currentExecution.getGlobalProblem());
                        gotc = result.getAssignment().calcCost(currentExecution.getGlobalProblem());
                        if (goodc != gotc) {
                            whenExpirementEndedBecauseOfWrongResults(result.getAssignment(), correct.getAssignment());
                            return;
                        }
                    } else if (result.hasSolution() == false && correct.hasSolution() == false) {
                        //great!
                    } else {
                        whenExpirementEndedBecauseOfWrongResults(result.getAssignment(), correct.getAssignment());
                        return;
                    }
                }
                
                whenSingleExecutionEndedSuccessfully(currentExecution);
                System.out.println("Good Solution :)");
            }
            whenExecutionEndedSuccessfully();
        } catch (Exception ex) {
            ex.printStackTrace();
            whenExpirementEndedBecauseExecutionCrushed(ex);
        }
    }

    protected abstract ExecutionResult safeSolve(Problem p);

    protected abstract void whenExpirementEndedBecauseExecutionCrushed(Exception ex);

    protected abstract void whenExecutionEndedSuccessfully();

    protected abstract void whenExpirementEndedBecauseOfWrongResults(Assignment wrong, Assignment right);

    protected abstract void whenSingleExecutionEndedSuccessfully(Execution execu);
}
