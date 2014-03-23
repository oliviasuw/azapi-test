/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api;

import bgu.dcr.az.common.io.StringBuilderWriter;
import java.io.PrintWriter;

/**
 *
 * @author bennyl
 */
public class TerminationReason {

    boolean error = false;
    Exception errorDescription = null;
    Proc misbehavingProcess = null;

    public TerminationReason(boolean error, Exception errorDescription, Proc misbehavingProcess) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.misbehavingProcess = misbehavingProcess;
    }

    public TerminationReason() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Exception getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(Exception errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Proc getMisbehavingProcess() {
        return misbehavingProcess;
    }

    public void setMisbehavingProcess(Proc misbehavingProcess) {
        this.misbehavingProcess = misbehavingProcess;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (errorDescription == null) {
            sb.append("null");
        } else {
            errorDescription.printStackTrace(new PrintWriter(new StringBuilderWriter(sb)));
        }
        return "TerminationReason{" + "error=" + error + ", errorDescription=" + sb.toString() + ", misbehavingProcess=" + misbehavingProcess + '}';
    }

}
