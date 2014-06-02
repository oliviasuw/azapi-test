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
        for (Property p : properties.values()) {
            PropertyValue value = p.get();
            if (value != null) {
                configureProperty(p, o, value);
            }
        }
    }

    @Override
    public void configureProperty(Object o, String propertyName, PropertyValue value) throws ConfigurationException {
        Property p = properties.get(propertyName);
        if (p == null) {
            throw new ConfigurationException("no such property: " + p.name());
        }

        configureProperty(p, o, value);
    }

    private void configureProperty(Property p, Object o, PropertyValue value) throws UnsupportedOperationException, ConfigurationException {

        if (p instanceof VariablePropertyImpl) {
            VariablePropertyImpl vprop = (VariablePropertyImpl) p;
            try {
                vprop.getField().set(o, value.create(p.typeInfo()));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new ConfigurationException("cannot configure object " + o, ex);
            }
        } else {
            PropertyImpl iprop = (PropertyImpl) p;
            if (iprop.isReadOnly()) {
                if (iprop.isCollection()) {
                    final Object val = accessor.invoke(o, iprop.getGetterIndex());
                    if (val == null) {
                        throw new UnsupportedOperationException("cannot configure property " + p.name() + " this property is a collection which has no initial value (get return null)");
                    }

//                    if (!(val instanceof Collection)) {
//                        throw new UnsupportedOperationException("cannot configure property " + p.name() + " its value type is collection but value of type " + val.getClass().getCanonicalName() + " is given (" + val + ")");
//                    }

                    Collection collection = (Collection) val;
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

    @Override
    public Configuration loadFrom(Object o) throws ConfigurationException {
        Object propertyValue = null;

        Property lastProp = null;
        try {
            for (Property p : properties.values()) {
                lastProp = p;
                
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
            throw new ConfigurationException("cannot configure property " + lastProp.name() + " in class: " + type.getSimpleName(), ex);
        }

        return this;
    }

}
