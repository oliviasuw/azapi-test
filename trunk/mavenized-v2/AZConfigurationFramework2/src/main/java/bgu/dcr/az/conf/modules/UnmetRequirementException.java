/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

/**
 *
 * @author bennyl
 */
public class UnmetRequirementException extends RuntimeException {

    public UnmetRequirementException() {
    }

    public UnmetRequirementException(String message) {
        super(message);
    }

    public UnmetRequirementException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnmetRequirementException(Throwable cause) {
        super(cause);
    }

}
