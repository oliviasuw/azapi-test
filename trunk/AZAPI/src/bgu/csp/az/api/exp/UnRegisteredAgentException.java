/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.exp;

/**
 *
 * @author bennyl
 */
public class UnRegisteredAgentException extends RuntimeException {

    public UnRegisteredAgentException(Throwable cause) {
        super(cause);
    }

    public UnRegisteredAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnRegisteredAgentException(String message) {
        super(message);
    }

    public UnRegisteredAgentException() {
    }
    
}
