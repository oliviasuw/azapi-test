/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api;

/**
 *
 * @author bennyl
 */
public class UnexpectedTerminationException extends RuntimeException {

    public UnexpectedTerminationException() {
    }

    public UnexpectedTerminationException(String message) {
        super(message);
    }

    public UnexpectedTerminationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedTerminationException(Throwable cause) {
        super(cause);
    }

}
