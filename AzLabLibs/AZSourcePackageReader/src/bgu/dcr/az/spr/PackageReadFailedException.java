/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.spr;

/**
 *
 * @author Administrator
 */
public class PackageReadFailedException extends Exception {

    public PackageReadFailedException() {
    }

    public PackageReadFailedException(String message) {
        super(message);
    }

    public PackageReadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackageReadFailedException(Throwable cause) {
        super(cause);
    }

    public PackageReadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
