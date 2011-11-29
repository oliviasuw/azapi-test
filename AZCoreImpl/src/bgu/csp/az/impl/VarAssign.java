/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
@Register(name = "assign")
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
