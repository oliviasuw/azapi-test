/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.experiment;

import bgu.dcr.az.conf.registery.Register;

/**
 *
 * @author User
 */
@Register("assign")
public class AlgorithemVariableAssignment {

    String propertyName;
    String value;

    public AlgorithemVariableAssignment() {
    }

    /**
     * @propertyName var
     * @return 
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @propertyName val
     * @return 
     */
    public String getValue() {
        return value;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + "var=" + propertyName + ", val=" + value + '}';
    }

    
    
}
