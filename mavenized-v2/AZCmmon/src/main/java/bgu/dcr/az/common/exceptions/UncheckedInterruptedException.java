/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author User
 */
public class UncheckedInterruptedException extends RuntimeException {

    private InterruptedException checked;

    public UncheckedInterruptedException(InterruptedException ex) {
        this.checked = ex;
        Thread.currentThread().interrupt(); //reapply the interruption flag..
    }

    @Override
    public String getMessage() {
        return checked.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return checked.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return checked.getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return checked.initCause(cause);
    }

    @Override
    public String toString() {
        return checked.toString();
    }

    @Override
    public void printStackTrace() {
        checked.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        checked.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        checked.printStackTrace(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return checked.fillInStackTrace();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return checked.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        checked.setStackTrace(stackTrace);
    }

}
