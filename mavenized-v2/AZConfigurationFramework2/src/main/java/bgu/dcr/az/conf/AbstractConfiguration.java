/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.api.Variable;
import bgu.dcr.az.conf.registery.Registery;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author User
 */
public abstract class AbstractConfiguration implements Configuration {

    protected Map<String, Property> properties = new HashMap<>();
    protected Class type;
    protected JavaDocInfo javadoc;
    protected MethodAccess accessor;
    protected ConstructorAccess cAccessor;
    
    @Override
    public Collection<Property> properties() {
        return properties.values();
    }

    @Override
    public Class configuredType() {
        return type;
    }

    @Override
    public String registeredName() {
        return Registery.get().getRegisteredClassName(type);
    }

    public Map<String, Property> propertiesMap() {
        return properties;
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
        for (Field f : configuredType().getDeclaredFields()) {
            f.setAccessible(true);
            final Variable ano = f.getAnnotation(Variable.class);
            if (ano != null) {
                Property p = new VariablePropertyImpl(ano.name(), this, f);
                properties.put(ano.name(), p);
            }
        }
    }


    @Override
    public <T> T create() throws ConfigurationException {
        T result = (T) cAccessor.newInstance();
        configure(result);
        return result;
    }

    @Override
    public void configure(Object o) throws ConfigurationException {
        try {
            for (Property p : properties.values()) {
                PropertyValue value = p.get();
                if (value != null) {
                    if (p instanceof VariablePropertyImpl) {
                        VariablePropertyImpl vprop = (VariablePropertyImpl) p;
                        vprop.getField().set(o, value.create(p.typeInfo()));
                    } else {
                        PropertyImpl iprop = (PropertyImpl) p;
                        if (iprop.isReadOnly()) {
                            if (iprop.isCollection()) {
                                Collection collection = (Collection) accessor.invoke(o, iprop.getGetterIndex());
                                if (collection == null) {
                                    throw new UnsupportedOperationException("cannot configure property " + p.name() + " this property is a collection which has no initial value (get return null)");
                                }

                                //TypeInfo elementType = iprop.typeInfo().getGenericParameters().isEmpty() ? JavaTypeParser.OBJECT_TYPE : iprop.typeInfo().getGenericParameters().get(0);
                                collection.clear();
                                collection.addAll(value.create(iprop.typeInfo()));
                            } else {
                                System.out.println("Ignoring configuration for " + p.name() + " since this property is readonly.");
                                //throw new UnsupportedOperationException("cannot configure property " + p.name() + " this property does not have setter and it is not a collection");
                            }
                        } else {
                            accessor.invoke(o, iprop.getSetterIndex(), new Object[]{value.create(iprop.typeInfo())});
                        }
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ConfigurationException("cannot configure object " + o, ex);
        }
    }

    @Override
    public Configuration loadFrom(Object o) throws ConfigurationException {
        Object propertyValue = null;
 
        try {
            for (Property p : properties.values()) {

                if (p instanceof VariablePropertyImpl) {
                    VariablePropertyImpl vp = (VariablePropertyImpl) p;
                    propertyValue = vp.getField().get(o);
                } else {
                    PropertyImpl ip = (PropertyImpl) p;
                    propertyValue = accessor.invoke(o, ip.getGetterIndex());
                }
                
                p.set(ConfigurationUtils.toPropertyValue(propertyValue));
            }
        } catch (Exception ex) {
            throw new ConfigurationException("cannot configure property @{p.name}", ex);
        }

        return this;
    }

}
