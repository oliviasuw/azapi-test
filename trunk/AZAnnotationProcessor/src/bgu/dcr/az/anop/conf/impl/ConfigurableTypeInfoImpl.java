/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.ConfigurableTypeInfo;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class ConfigurableTypeInfoImpl implements ConfigurableTypeInfo {
    private Class clazz;
    private final List<ConfigurableTypeInfoImpl> subtypes;

    public ConfigurableTypeInfoImpl(String className) {
        try {
            this.clazz = Class.forName(className);
        } catch (Exception e) {
            this.clazz = Object.class;
        }
        subtypes = new LinkedList<>();
    }

    @Override
    public Class getType() {
        return clazz;
    }

    @Override
    public List<ConfigurableTypeInfoImpl> getGenericParameters() {
        return subtypes;
    }

    public void addGenericParameter(ConfigurableTypeInfoImpl type) {
        subtypes.add(type);
    }

    public void addAll(Collection<ConfigurableTypeInfoImpl> types) {
        subtypes.addAll(types);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(clazz.getCanonicalName());
        if (!subtypes.isEmpty()) {
            sb.append("<");
            for (ConfigurableTypeInfoImpl t : subtypes) {
                sb.append(t.toString()).append(", ");
            }
            sb.delete(sb.length() - ", ".length(), sb.length());
            sb.append(">");
        }
        return sb.toString();
    }
    
}
