/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution.manipulators;

import bgu.dcr.az.conf.api.JavaDocInfo;

/**
 *
 * @author User
 */
public class HandlerInfo {

    private JavaDocInfo doc;
    private String of;
    private ParameterInfo[] parameters;

    public HandlerInfo(JavaDocInfo doc, String of, ParameterInfo[] parameters) {
        this.doc = doc;
        this.of = of;
    }

    public JavaDocInfo doc() {
        return doc;
    }

    public String hanlderOf() {
        return of;
    }

    public ParameterInfo[] parameters() {
        return parameters;
    }

}
