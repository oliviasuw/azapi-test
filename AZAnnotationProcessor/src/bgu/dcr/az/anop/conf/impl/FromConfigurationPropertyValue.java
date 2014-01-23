/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.ConfigurableTypeInfo;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.PropertyValue;

/**
 *
 * @author User
 */
public class FromConfigurationPropertyValue implements PropertyValue {

    private Configuration value;

    public FromConfigurationPropertyValue(Configuration conf) {
        this.value = conf;
    }

    public Configuration getValue() {
        return value;
    }

    @Override
    public <T> T create(ConfigurableTypeInfo type) throws ConfigurationException {
        if (type.getType().isAssignableFrom(value.typeInfo().getType())) {
            return value.create();
        } else {
            throw new ConfigurationException(String.format("configuration type: %s not match the type of the assigned property: %s", type.getType().getCanonicalName(), value.typeInfo().getType().getCanonicalName()));
        }
    }

}
