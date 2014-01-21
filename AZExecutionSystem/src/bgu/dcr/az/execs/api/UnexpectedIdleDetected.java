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
public class UnexpectedIdleDetected extends RuntimeException {

    public UnexpectedIdleDetected() {
    }

    public UnexpectedIdleDetected(String message) {
        super(message);
    }

    public UnexpectedIdleDetected(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedIdleDetected(Throwable cause) {
        super(cause);
    }

}
