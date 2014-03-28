/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf;

import bgu.dcr.az.common.reflections.ReflectionUtils;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.api.TypeInfo;
import bgu.dcr.az.conf.utils.JavaTypeParser;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class FromCollectionPropertyValue implements PropertyValue, Iterable<PropertyValue> {

    private final List<PropertyValue> values = new LinkedList<>();

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
    
    public FromCollectionPropertyValue removeAll(Collection<PropertyValue> properties) {
        values.removeAll(properties);
        return this;
    }

    @Override
    public <T> T create(TypeInfo type) throws ConfigurationException {
        try {
            Collection c = new LinkedList();

            TypeInfo innerType = type.getGenericParameters().isEmpty() ? JavaTypeParser.parse("java.lang.Object") : type.getGenericParameters().get(0);
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

    @Override
    public String stringValue() {
        StringBuilder sb = new StringBuilder("(");
        for (PropertyValue v : values) {
            sb.append(v.stringValue()).append(", ");
        }

        return sb.deleteCharAt(sb.length()).deleteCharAt(sb.length()).append(")").toString();
    }

}
