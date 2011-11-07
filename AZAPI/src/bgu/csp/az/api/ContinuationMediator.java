/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

/**
 *
 * @author bennyl
 */
public class ContinuationMediator {
    
    Continuation continuation;
    
    public void andWhenDoneDo(Continuation c){
        this.continuation = c;
    }

    public void executeContinuation() {
        this.continuation.doContinue();
    }
    
}
