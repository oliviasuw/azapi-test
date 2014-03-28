/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.api.TypeInfo;

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
    public <T> T create(TypeInfo type) throws ConfigurationException {
        if (type.getType().isAssignableFrom(value.configuredType())) {
            return value.create();
        } else {
            throw new ConfigurationException(String.format("configuration type: %s not match the type of the assigned property: %s", type.getType().getCanonicalName(), value.configuredType().getCanonicalName()));
        }
    }

    @Override
    public String stringValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Property p : value) {
            sb.append("[").append(p.name()).append(" = ").append(p.stringValue()).append("]");
        }

        return sb.append("}").toString();

    }

}
