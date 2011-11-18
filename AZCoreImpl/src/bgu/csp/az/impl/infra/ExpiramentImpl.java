/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.CorrectnessTester;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.Expirement;
import bgu.csp.az.api.infra.Round;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author bennyl
 */
public class ExpiramentImpl extends AbstractProcess implements Expirement {

    private static final VariableMetadata[] EMPTY_VARIABLE_ARRAY =  new VariableMetadata[0];
    
    private List<Round> rounds = new ArrayList<Round>();
    private ExpirementResult result;
    
    @Override
    public void _run() {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        for (Round current : rounds){
            if (Thread.interrupted()){
                result = new ExpirementResult(true);
            }
            
            if (current.canAccept(CorrectnessTester.class)){
                current.addSubConfiguration(current.getProblemGenerator().getType().getCorrectnessTester());
            }
            
            if (current instanceof AbstractRound){
                ((AbstractRound)current).setPool(pool);
            }
            
            current.run();
            RoundResult res = current.getResult();
            switch (res.finishStatus){
                case CRUSH:
                case WRONG_RESULT:
                    result = new ExpirementResult(current, res);
                    return;
            }
        }
//        try {
//            
//            
//            while (hasMoreExecutions() && !Thread.currentThread().isInterrupted()) {
//                currentExecution = nextExecution();
//                currentExecutionEventPipe = new EventPipe();
//                currentExecution.setEventPipe(currentExecutionEventPipe);
//                p = currentExecution.getGlobalProblem();
//
//                currentExecution.run();
//
//                result = currentExecution.getResult();
//                
//                //CHECK CRUSH
//                if (result.isExecutionCrushed()){
//                    whenExpirementEndedBecauseExecutionCrushed(result.getCrushReason());
//                    return;
//                }
//                
//                //CHECK CORRECTNESS
//                System.out.println("Testing solution...");
//                correct = safeSolve(currentExecution.getGlobalProblem());
//                if (correct != null) {
//                    if (result.hasSolution() == true && correct.hasSolution() == true) {
//                        goodc = correct.getAssignment().calcCost(currentExecution.getGlobalProblem());
//                        gotc = result.getAssignment().calcCost(currentExecution.getGlobalProblem());
//                        if (goodc != gotc) {
//                            whenExpirementEndedBecauseOfWrongResults(result.getAssignment(), correct.getAssignment());
//                            return;
//                        }
//                    } else if (result.hasSolution() == false && correct.hasSolution() == false) {
//                        //great!
//                    } else {
//                        whenExpirementEndedBecauseOfWrongResults(result.getAssignment(), correct.getAssignment());
//                        return;
//                    }
//                }
//                
//                whenSingleExecutionEndedSuccessfully(currentExecution);
//                System.out.println("Good Solution :)");
//                //SAVE DATA TO DATABASE - SHOULD BE DONE ON A DIFFERENT THREAD
//            }
//            whenExpirementEndedSuccessfully();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            whenExpirementEndedBecauseExecutionCrushed(ex);
//        }
    }

    @Override
    public void addRound(Round round) {
        rounds.add(round);
    }

    @Override
    public List<Round> getRounds() {
        return Collections.unmodifiableList(rounds);
    }

    @Override
    public ExpirementResult getResult() {
        return result;
    }

    @Override
    public String getConfigurationName() {
        return "expirement";
    }

    @Override
    public String getConfigurationDescription() {
        return "configureable collection of rounds";
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return EMPTY_VARIABLE_ARRAY; 
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        Class<? extends Configureable> ret = Round.class;
        LinkedList<Class<? extends Configureable>> ll = new LinkedList<Class<? extends Configureable>>();
        ll.add(ret);
        return ll;
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        return Round.class.isAssignableFrom(cls);
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (! canAccept(sub.getClass())){
            throw new InvalidValueException("only except rounds");
        }else {
            Round r = (Round) sub;
            addRound(r);
        }
    }

    @Override
    public void configure(Map<String, Object> variables) {
        //NO VARIABLES!
    }
    
}
