/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.JavaDocInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocInfoImpl implements JavaDocInfo {
    public static final JavaDocInfo EMPTY_JAVADOC = new JavaDocInfoImpl();

    private final Map<String, List<String>> data;
    private final String description;

    private JavaDocInfoImpl() {
        this.data = Collections.EMPTY_MAP;
        this.description = "";
    }

    public JavaDocInfoImpl(Map<String, List<String>> data) {
        this.data = data;
        final List<String> descTemp = data.remove("");
        description = descTemp == null || descTemp.isEmpty() ? "" : descTemp.get(0);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Collection<String> tag(String tag) {
        final List<String> got = data.get(tag);
        return got == null ? Collections.EMPTY_LIST : got;
    }

    @Override
    public Collection<String> tags() {
        return data.keySet();
    }

    @Override
    public Iterator<String> iterator() {
        return data.keySet().iterator();
    }

    @Override
    public String first(String tag) {
        if (!data.containsKey(tag)) {
            return null;
        }
        return data.get(tag).get(0);
    }

}
