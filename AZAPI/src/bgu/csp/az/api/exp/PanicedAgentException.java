/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.exp;

/**
 *
 * @author bennyl
 */
public class PanicedAgentException extends RuntimeException{

    /**
     * 
     * @param cause
     */
    public PanicedAgentException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public PanicedAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     * @param message
     */
    public PanicedAgentException(String message) {
        super(message);
    }

    /**
     * 
     */
    public PanicedAgentException() {
    }
    
}
