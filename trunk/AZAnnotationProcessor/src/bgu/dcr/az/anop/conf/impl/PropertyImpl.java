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
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.VisualData;

/**
 *
 * @author User
 */
public class PropertyImpl implements Property {

    private String name;
    private Configuration parent;
    private ConfigurableTypeInfo type;
    private PropertyValue propertyValue;
    private JavaDocInfo javadoc;

    public PropertyImpl(String name, Configuration parent, ConfigurableTypeInfo type, JavaDocInfo javadoc) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.javadoc = javadoc;
    }

    public void setJavadoc(JavaDocInfo javadoc) {
        this.javadoc = javadoc;
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
    public ConfigurableTypeInfo typeInfo() {
        return this.type;
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

}
