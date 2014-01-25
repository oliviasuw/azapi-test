/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf;

import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.impl.FromCollectionPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Text;

/**
 *
 * @author User
 */
public class ConfigurationUtils {

    public static Element toXML(Configuration conf) throws ConfigurationException {
        final String registeredClassName = RegisteryUtils.getDefaultRegistery().getRegisteredClassName(conf.typeInfo().getType());
        if (registeredClassName == null) {
            throw new ConfigurationException("cannot find registration for class: " + conf.typeInfo().getType().getCanonicalName());
        }
        Element e = new Element(registeredClassName);

        for (Property p : conf.properties()) {
            putProperty(p.name(), p.get(), e, true);
        }

        return e;
    }

    public static Configuration fromXML(Element e) throws ClassNotFoundException {
        Configuration c = RegisteryUtils.getDefaultRegistery().getConfiguration(e.getLocalName());
        
        if (c == null) throw new ClassNotFoundException("cannot find class registration for element " + e.getLocalName());

        fillPropertiesFromAttributes(c, e);

        fillPropertiesFromChildren(c, e);

        return c;
    }

    private static boolean putProperty(String property, PropertyValue value, Element e, boolean allowAttributes) throws ConfigurationException, UnsupportedOperationException {
        if (value == null) {
            return false;
        }
        if (value instanceof FromStringPropertyValue) {
            putProperty(property, (FromStringPropertyValue) value, e, allowAttributes);
        } else if (value instanceof FromConfigurationPropertyValue) {
            putProperty(property, (FromConfigurationPropertyValue) value, e, allowAttributes);
        } else if (value instanceof FromCollectionPropertyValue) {
            putProperty(property, (FromCollectionPropertyValue) value, e, allowAttributes);
        } else {
            throw new UnsupportedOperationException("does not know how to save value of type: " + value.getClass().getSimpleName());
        }
        return true;
    }

    private static void putProperty(String property, FromStringPropertyValue value, Element e, boolean allowAttributes) {
        if (allowAttributes) {
            e.addAttribute(new Attribute(property, value.getValue()));
        } else {
            Element inner = new Element(property);
            inner.appendChild(value.getValue());
            e.appendChild(inner);
        }
    }

    private static void putProperty(String property, FromConfigurationPropertyValue value, Element e, boolean allowAttributes) throws ConfigurationException {
        Element inner = toXML(value.getValue());

        Element propElem = new Element(property);
        propElem.appendChild(inner);
        e.appendChild(propElem);
    }

    private static void putProperty(String property, FromCollectionPropertyValue value, Element e, boolean allowAttributes) throws ConfigurationException {
        Element innerElement = new Element(property);

        for (PropertyValue c : value) {
            putProperty("item", c, innerElement, false);
        }

        e.appendChild(innerElement);
    }

    private static void fillPropertiesFromAttributes(Configuration c, Element e) {
        for (int i = 0; i < e.getAttributeCount(); i++) {
            Attribute attr = e.getAttribute(i);
            Property property = c.get(attr.getLocalName());
            if (property != null) {
                property.set(new FromStringPropertyValue(attr.getValue()));
            } else {
                System.err.println("doesnt know what to do with attribute " + attr.getLocalName() + ", ignoring...");
            }
        }
    }

    private static void fillPropertiesFromChildren(Configuration c, Element e) throws ClassNotFoundException {
        Elements elements = e.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            Element child = elements.get(i);

            Property property = resolvePropertyFromElement(c, child);

            if (property == null) {
                System.err.println("does not know what to do with child " + child.getLocalName() + ", ignoring...");
            }
        }
    }

    private static Property resolvePropertyFromElement(Configuration c, Element child) throws ClassNotFoundException {
        Property property = c.get(child.getLocalName());
        if (property == null) { //attempt to find property by type
            Class valueType = RegisteryUtils.getDefaultRegistery().getRegisteredClassByName(child.getLocalName());
            if (valueType != null) {
                for (Property p : c) {
                    if (p.typeInfo().getType().isAssignableFrom(valueType)) {
                        property = p;

                        PropertyValue value = resolvePropertyValueFromConfigurationElement(child);
                        p.set(value);

                        break;
                    } else if (!p.typeInfo().getGenericParameters().isEmpty() && p.typeInfo().getGenericParameters().get(0).getType().isAssignableFrom(valueType)) {
                        property = p;

                        PropertyValue value = resolvePropertyValueFromConfigurationElement(child);
                        FromCollectionPropertyValue values = (FromCollectionPropertyValue) p.get();

                        if (values == null) {
                            p.set(values = new FromCollectionPropertyValue());
                        }

                        values.add(value);
                        break;
                    }
                }
            }

            if (property == null) {
                System.err.println("does not know what to do with element: " + child.getLocalName() + ", ignoring...");
            }

        } else {
            PropertyValue value = resolvePropertyValueFromPropertyNode(c, property.name(), child);
            property.set(value);
        }

        return property;
    }

    private static PropertyValue resolvePropertyValueFromPropertyNode(Configuration c, String property, Element n) throws ClassNotFoundException {
        if (n.getChildCount() == 0) {
            System.err.println("found empty property decleration " + property + ", ignoring...");
            return null;
        } else if (n.getChildCount() == 1 && n.getChild(0) instanceof Text) {
            return resolvePropertyValueFromTextualNode(c, property, (Text) n.getChild(0));
        } else {
            return resolvePropertyValueFromChildElements(c, property, n.getChildElements());
        }
    }

    private static PropertyValue resolvePropertyValueFromTextualNode(Configuration c, String property, Text text) {
        return new FromStringPropertyValue(text.getValue());
    }

    private static PropertyValue resolvePropertyValueFromChildElements(Configuration c, String property, Elements childElements) throws ClassNotFoundException {
        if (Collection.class.isAssignableFrom(c.get(property).typeInfo().getType())) {
            List<PropertyValue> values = resolvePropertyValuesFromChildElements(c, property, childElements);
            FromCollectionPropertyValue value = new FromCollectionPropertyValue();
            value.addAll(values);
            return value;
        } else {
            if (childElements.size() == 0) {
                System.err.println("found empty property decleration " + property + ", ignoring...");
            }

            if (childElements.size() > 1) {
                System.err.println("found more than one value for " + property + ", using the first...");
            }

            return resolvePropertyValueFromConfigurationElement(childElements.get(0));
        }
    }

    private static List<PropertyValue> resolvePropertyValuesFromChildElements(Configuration c, String property, Elements childElements) throws ClassNotFoundException {
        List<PropertyValue> result = new LinkedList<>();
        for (int i = 0; i < childElements.size(); i++) {
            Element e = childElements.get(i);
            PropertyValue value = null;
            if (e.getLocalName().equals("item")) {
                value = resolvePropertyValueFromPropertyNode(c, property + "*", e);
            } else {
                value = resolvePropertyValueFromConfigurationElement(e);
            }

            if (value != null) {
                result.add(value);
            }
        }

        return result;
    }

    private static PropertyValue resolvePropertyValueFromConfigurationElement(Element e) throws ClassNotFoundException {
        return new FromConfigurationPropertyValue(fromXML(e));
    }

    public static Configuration createConfigurationFor(Object o) throws ClassNotFoundException {
        Configuration conf = RegisteryUtils.getDefaultRegistery().getConfiguration(o.getClass());
        return conf;
    }

}
