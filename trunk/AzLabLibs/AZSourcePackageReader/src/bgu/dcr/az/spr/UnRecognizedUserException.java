/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.spr;

/**
 *
 * @author Administrator
 */
public class UnRecognizedUserException extends Exception{

    public UnRecognizedUserException() {
    }

    public UnRecognizedUserException(String message) {
        super(message);
    }

    public UnRecognizedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnRecognizedUserException(Throwable cause) {
        super(cause);
    }

    public UnRecognizedUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
