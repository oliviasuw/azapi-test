/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

/**
 *
 * @author Zovadi
 */
public class UnavailableFieldException extends Exception {

    public UnavailableFieldException() {
    }

    public UnavailableFieldException(String message) {
        super(message);
    }

    public UnavailableFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableFieldException(Throwable cause) {
        super(cause);
    }
}
