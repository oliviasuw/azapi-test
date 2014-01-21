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
public class ProcessDoesntExistsException extends RuntimeException {

    public ProcessDoesntExistsException() {
    }

    public ProcessDoesntExistsException(String message) {
        super(message);
    }

    public ProcessDoesntExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessDoesntExistsException(Throwable cause) {
        super(cause);
    }

}
