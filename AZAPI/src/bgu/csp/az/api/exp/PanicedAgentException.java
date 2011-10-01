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

    public PanicedAgentException(Throwable cause) {
        super(cause);
    }

    public PanicedAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PanicedAgentException(String message) {
        super(message);
    }

    public PanicedAgentException() {
    }
    
}
