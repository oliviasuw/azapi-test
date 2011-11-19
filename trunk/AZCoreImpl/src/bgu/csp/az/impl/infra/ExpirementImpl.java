/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.ano.Register;
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
@Register(name="expirement")
public class ExpirementImpl extends AbstractProcess implements Expirement {

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
                current.addSubConfiguration(new DefaultCorrectnessTester());
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
        
        result = new ExpirementResult(false);
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
