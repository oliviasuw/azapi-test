/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.Configuration;
import java.lang.reflect.Field;

/**
 *
 * @author User
 */
public class VariablePropertyImpl extends PropertyImpl {

    Field f;

    public VariablePropertyImpl(String name, Configuration parent, Field f) {
        super(name, parent, new ConfigurableTypeInfoImpl(f.getType()), JavaDocInfoImpl.EMPTY_JAVADOC);
        this.f = f;
    }

    public Field getField() {
        return f;
    }

}
