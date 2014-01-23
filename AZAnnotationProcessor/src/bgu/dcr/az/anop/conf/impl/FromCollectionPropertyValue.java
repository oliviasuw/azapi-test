/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.ConfigurableTypeInfo;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.utils.JavaTypeParser;
import bgu.dcr.az.anop.utils.ReflectionUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class FromCollectionPropertyValue implements PropertyValue, Iterable<PropertyValue> {

    private List<PropertyValue> values = new LinkedList<>();

    public FromCollectionPropertyValue add(PropertyValue value) {
        values.add(value);
        return this;
    }
    
    public FromCollectionPropertyValue addAll(Collection<PropertyValue> values) {
        this.values.addAll(values);
        return this;
    }

    public FromCollectionPropertyValue remove(PropertyValue value) {
        values.remove(value);
        return this;
    }

    @Override
    public <T> T create(ConfigurableTypeInfo type) throws ConfigurationException {
        try {
            Collection c = ReflectionUtils.createSuitableCollection(type.getType());

            ConfigurableTypeInfo innerType = type.getGenericParameters().isEmpty() ? JavaTypeParser.parse("java.lang.Object") : type.getGenericParameters().get(0);
            for (PropertyValue e : this) {
                c.add(e.create(innerType));
            }

            return (T) c;
        } catch (UnsupportedOperationException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        }
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return values.iterator();
    }

}
