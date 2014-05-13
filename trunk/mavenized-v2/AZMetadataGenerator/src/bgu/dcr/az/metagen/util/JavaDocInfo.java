/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.metagen.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocInfo implements Iterable<String>{

    private final Map<String, List<String>> data;
    private final String description;
    private final String original;


    public JavaDocInfo(Map<String, List<String>> data, String original) {
        this.data = data;
        final List<String> descTemp = data.remove("");
        description = descTemp == null || descTemp.isEmpty() ? "" : descTemp.get(0);
        this.original = original;
    }

    public String description() {
        return description;
    }

    public Collection<String> tag(String tag) {
        final List<String> got = data.get(tag);
        return got == null ? Collections.EMPTY_LIST : got;
    }

    public Collection<String> tags() {
        return data.keySet();
    }

    @Override
    public Iterator<String> iterator() {
        return data.keySet().iterator();
    }

    public String first(String tag) {
        if (!data.containsKey(tag)) {
            return null;
        }
        return data.get(tag).get(0);
    }

    public String toString() {
        return original == null ? "" : TemplateUtils.escape(original.trim());
    }

}
