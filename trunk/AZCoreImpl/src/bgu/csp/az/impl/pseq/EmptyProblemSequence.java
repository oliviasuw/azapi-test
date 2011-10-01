/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pseq;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemSequence;

/**
 * the most simple problem sequence - an empty one used for initialization
 * @author bennyl
 */
public class EmptyProblemSequence implements ProblemSequence{

    @Override
    public Problem next() {
        return null;
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean hasNext() {
        return false;
    }
    
}
