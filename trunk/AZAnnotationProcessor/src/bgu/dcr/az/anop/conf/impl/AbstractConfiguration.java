/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.TypeInfo;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.Variable;
import bgu.dcr.az.anop.conf.VisualData;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public abstract class AbstractConfiguration implements Configuration {

    protected Map<String, Property> properties;
    protected TypeInfo type;
    protected VisualData vdata;
    protected JavaDocInfo javadoc;


    @Override
    public Collection<Property> properties() {
        return properties.values();
    }

    @Override
    public TypeInfo typeInfo() {
        return type;
    }

    public Map<String, Property> propertiesMap() {
        return properties;
    }

    @Override
    public VisualData visualData() {
        return vdata;
    }

    @Override
    public Property get(String name) {
        return properties.get(name);
    }

    @Override
    public Iterator<Property> iterator() {
        return properties.values().iterator();
    }

    @Override
    public JavaDocInfo doc() {
        return javadoc;
    }

    protected void scanVariables() {
//        System.out.println("scanning variables");
        for (Field f : typeInfo().getType().getDeclaredFields()) {
            f.setAccessible(true);
            final Variable ano = f.getAnnotation(Variable.class);
            if (ano != null) {
                Property p = new VariablePropertyImpl(ano.name(), this, f);
//                System.out.println("found variable property: " + ano.name());
                properties.put(ano.name(), p);
            }
        }
    }

    protected void configureVariables(Object o) throws ConfigurationException {
        for (Property p : properties.values()) {
            if (p instanceof VariablePropertyImpl) {
                if (p.get() != null) {
                    try {
                        VariablePropertyImpl vp = (VariablePropertyImpl) p;
                        vp.getField().set(o, vp.get().create(vp.typeInfo()));
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new ConfigurationException("cannot configure property for variable: " + p.name(), ex);
                    }
                }
            }
        }
    }

}
