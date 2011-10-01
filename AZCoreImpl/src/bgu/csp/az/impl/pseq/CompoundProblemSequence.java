/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pseq;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemSequence;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class CompoundProblemSequence implements ProblemSequence{
    List<ProblemSequence> pseqs;

    public CompoundProblemSequence() {
        pseqs = new LinkedList<ProblemSequence>();
    }
    
    public CompoundProblemSequence append(ProblemSequence pseq){
        pseqs.add(pseq);
        return this;
    }

    @Override
    public Problem next() {
        if (pseqs.size() > 0) {
            if (pseqs.get(0).hasNext()){
                return pseqs.get(0).next();
            }else{
                pseqs.remove(0);
                return next();
            }
        }else {
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        if (pseqs.isEmpty()) return false;
        if (pseqs.get(0).hasNext()) return true;
        else {
            pseqs.remove(0);
            return hasNext();
        }
    }
    
    
}
