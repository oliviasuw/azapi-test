/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
@Register(name = "assign", display="Variable Assignment")
public class VarAssign extends AbstractConfigureable {

    @Variable(name = "var", description = "the variable name")
    String varName;
    @Variable(name = "val", description = "the variable value to assign")
    String value;

    @Override
    protected void configurationDone() {
    }

    public String getVarName() {
        return varName;
    }

    public String getValue() {
        return value;
    }
    
}
