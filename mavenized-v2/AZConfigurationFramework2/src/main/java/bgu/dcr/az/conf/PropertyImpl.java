/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.api.TypeInfo;
import java.util.Collection;

/**
 *
 * @author User
 */
public class PropertyImpl implements Property {

    private final String name;
    private final Configuration parent;
    private final TypeInfo type;
    private PropertyValue propertyValue;
    private JavaDocInfo javadoc;
    private int getterIndex;
    private int setterIndex;

    public PropertyImpl(String name, Configuration parent, TypeInfo type, JavaDocInfo javadoc, int getterIndex, int setterIndex) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.javadoc = javadoc;
        this.getterIndex = getterIndex;
        this.setterIndex = setterIndex;
    }

    public void setJavadoc(JavaDocInfo javadoc) {
        this.javadoc = javadoc;
    }

    public int getGetterIndex() {
        return getterIndex;
    }

    public int getSetterIndex() {
        return setterIndex;
    }
    
    public boolean isReadOnly(){
        return setterIndex == -1;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Configuration parent() {
        return this.parent;
    }

    @Override
    public TypeInfo typeInfo() {
        return this.type;
    }
    
    public boolean isCollection(){
        return Collection.class.isAssignableFrom(type.getType());
    }
    
    @Override
    public void set(PropertyValue cv) {
        this.propertyValue = cv;
    }

    @Override
    public PropertyValue get() {
        return this.propertyValue;
    }

    @Override
    public JavaDocInfo doc() {
        return javadoc;
    }

    @Override
    public String toString() {
        return "PropertyImpl{" + "name=" + name + ", type=" + type + '}';
    }

    @Override
    public String stringValue() {
        if (this.propertyValue == null) {
            return "null";
        }
        return this.propertyValue.stringValue();
    }

}
