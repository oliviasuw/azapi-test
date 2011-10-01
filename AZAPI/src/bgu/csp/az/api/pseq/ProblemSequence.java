/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.pseq;

import bgu.csp.az.api.Problem;

/**
 * interface for a stream of problems
 * 
 * INCOMPLEATE API - would change in the future
 * 
 * @author bennyl
 */
public interface ProblemSequence {
    /**
     * @return the next message in the stream
     */
    Problem next();
    
    /**
     * 
     * @return true if there are more problems in the stream
     */
    boolean hasNext();
    
}
