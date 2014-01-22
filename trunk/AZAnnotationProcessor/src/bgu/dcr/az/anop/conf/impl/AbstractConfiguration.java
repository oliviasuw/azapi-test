/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.ConfigurableTypeInfo;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.VisualData;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author User
 */
public abstract class AbstractConfiguration implements Configuration {

    protected Map<String, Property> properties;
    protected ConfigurableTypeInfo type;
    protected VisualData vdata;
    protected JavaDocInfo javadoc;

    @Override
    public Collection<Property> properties() {
        return properties.values();
    }

    @Override
    public ConfigurableTypeInfo type() {
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

}
