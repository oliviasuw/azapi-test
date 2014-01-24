/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.anop.algo.impl;

import bgu.dcr.az.anop.algo.ParameterInfo;
import bgu.dcr.az.anop.conf.TypeInfo;

/**
 *
 * @author User
 */
public class ParameterInfoImpl implements ParameterInfo{

    private String name;
    private TypeInfo type;

    public ParameterInfoImpl(String name, TypeInfo info) {
        this.name = name;
        this.type = info;
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public TypeInfo typeInfo() {
        return type;
    }
    
}
