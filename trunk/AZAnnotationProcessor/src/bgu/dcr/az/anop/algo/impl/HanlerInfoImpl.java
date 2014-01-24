/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.algo.impl;

import bgu.dcr.az.anop.algo.HandlerInfo;
import bgu.dcr.az.anop.algo.ParameterInfo;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import java.util.Collection;

/**
 *
 * @author User
 */
public class HanlerInfoImpl implements HandlerInfo {

    private JavaDocInfo doc;
    private String of;
    private ParameterInfo[] parameters;

    public HanlerInfoImpl(JavaDocInfo doc, String of, ParameterInfo[] parameters) {
        this.doc = doc;
        this.of = of;
    }

    @Override
    public JavaDocInfo doc() {
        return doc;
    }

    @Override
    public String hanlderOf() {
        return of;
    }

    @Override
    public ParameterInfo[] parameters() {
        return parameters;
    }

}
