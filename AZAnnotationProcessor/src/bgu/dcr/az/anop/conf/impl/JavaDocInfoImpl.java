/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.JavaDocInfo;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocInfoImpl implements JavaDocInfo {

    Map<String, String> data;
    String description;

    public JavaDocInfoImpl(Map<String, String> data) {
        this.data = data;
        description = data.remove("");
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String tag(String tag) {
        return data.get(tag);
    }

    @Override
    public Collection<String> tags() {
        return data.keySet();
    }

    @Override
    public Iterator<String> iterator() {
        return data.keySet().iterator();
    }

}
