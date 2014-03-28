/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.exceptions;

/**
 *
 * @author User
 */
public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException() {
    }

    public UncheckedSQLException(String message) {
        super(message);
    }

    public UncheckedSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedSQLException(Throwable cause) {
        super(cause);
    }

}
