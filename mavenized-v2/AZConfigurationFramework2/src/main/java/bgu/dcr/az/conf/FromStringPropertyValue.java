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

/**
 *
 * @author User
 */
public class FromStringPropertyValue implements PropertyValue {

    private final String value;

    public FromStringPropertyValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <T> T create(TypeInfo type) throws ConfigurationException {
        try {
            return (T) ReflectionUtils.valueOf(value, type.getType());
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException("cannot create value of type: " + type + " from the string: " + value + ", valueOf method not exists", ex);
        }
    }

    @Override
    public String stringValue() {
        return value;
    }

}
