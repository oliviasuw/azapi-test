/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.dcr.execution.manipulators;

import bgu.dcr.az.conf.api.TypeInfo;

/**
 *
 * @author User
 */
public class ParameterInfo {

    private String name;
    private TypeInfo type;

    public ParameterInfo(String name, TypeInfo info) {
        this.name = name;
        this.type = info;
    }
    
    public String name() {
        return name;
    }

    public TypeInfo typeInfo() {
        return type;
    }
    
}
