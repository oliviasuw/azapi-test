/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exceptions;

/**
 *
 * @author User
 */
public class ExperimentExecutionException extends Exception {

    public ExperimentExecutionException() {
    }

    public ExperimentExecutionException(String message) {
        super(message);
    }

    public ExperimentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExperimentExecutionException(Throwable cause) {
        super(cause);
    }

}
