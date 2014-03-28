/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.utils;

import bgu.dcr.az.common.io.StringBuilderStream;
import bgu.dcr.az.conf.FromCollectionPropertyValue;
import bgu.dcr.az.conf.FromConfigurationPropertyValue;
import bgu.dcr.az.conf.FromStringPropertyValue;
import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.registery.Registery;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 *
 * @author User
 */
public class ConfigurationUtils {

    public static Element toConfigurationXML(Object o) throws ConfigurationException {
        try {
            return toXML(load(o));
        } catch (ClassNotFoundException ex) {
            throw new ConfigurationException("cannot load configuration", ex);
        }
    }

    public static String toConfigurationXMLString(Object o) throws ConfigurationException {
        StringBuilderStream writter = new StringBuilderStream();
        try (PrintStream pw = new PrintStream(writter)) {
            Element conf = toConfigurationXML(o);

            Serializer serializer = new Serializer(pw, "UTF-8");
            serializer.setIndent(4);
            serializer.write(new Document(conf));
            serializer.flush();
        } catch (IOException ex) {
            throw new ConfigurationException("cannot write configuration");
        }

        return writter.toString();
    }

    public static Element toXML(Configuration conf) throws ConfigurationException {
        final String registeredClassName = Registery.get().getRegisteredClassName(conf.configuredType());
        if (registeredClassName == null) {
            throw new ConfigurationException("cannot find registration for class: " + conf.configuredType().getCanonicalName());
        }
        
        Element e = new Element(registeredClassName);

        for (Property p : conf.properties()) {
            putProperty(p.name(), p.get(), e, true);
        }

        return e;
    }

    public static Configuration fromXML(Element e) throws ClassNotFoundException {
        Configuration c = Registery.get().getConfiguration(e.getLocalName());

        if (c == null) {
            throw new ClassNotFoundException("cannot find class registration for element " + e.getLocalName());
        }

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

    private static void putProperty(String property, FromStringPropertyValue value, Element e, boolean allowAttributes) throws ConfigurationException {
        if (allowAttributes) {
            try {
                e.addAttribute(new Attribute(property, value.getValue()));
            } catch (nu.xom.IllegalCharacterDataException ex) {
                throw new ConfigurationException("cannot write attribute:  " + property + " with value: " + value.getValue(), ex);
            }
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
            if (!(c instanceof FromConfigurationPropertyValue)) {
                putProperty("item", c, innerElement, false);
            } else {
                Element inner = toXML(((FromConfigurationPropertyValue) c).getValue());
                innerElement.appendChild(inner);
            }
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
            Class valueType = Registery.get().getRegisteredClassByName(child.getLocalName());
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

    /**
     * 
     * @param o
     * @return
     * @throws ClassNotFoundException 
     * @deprecated should remove before release
     */
    public static Configuration createConfigurationFor(Object o) throws ClassNotFoundException {
        Configuration conf = Registery.get().getConfiguration(o.getClass());
        return conf;
    }

    public static Configuration get(Class type) throws ClassNotFoundException{
        return Registery.get().getConfiguration(type);
    }
    
    public static Configuration load(Object o) throws ClassNotFoundException, ConfigurationException {
        return createConfigurationFor(o).loadFrom(o);
    }

    public static void write(Object configurable, File problemFile) throws IOException {
        try (PrintStream pw = new PrintStream(problemFile)) {
            Configuration conf = createConfigurationFor(configurable).loadFrom(configurable);

            Serializer serializer = new Serializer(pw, "UTF-8");
            serializer.setIndent(4);
            serializer.write(new Document(toXML(conf)));
            serializer.flush();
        } catch (ClassNotFoundException | ConfigurationException ex) {
            throw new IOException("cannot write configuration", ex);
        }

    }

    public static boolean isConfigurable(Class c) {
        return !Registery.get().getImplementors(c).isEmpty();
    }

    public static PropertyValue toPropertyValue(Object o) throws ConfigurationException {
        if (o == null) {
            return null;
        }

        if (o instanceof Collection) {
            FromCollectionPropertyValue value = new FromCollectionPropertyValue();
            Collection co = (Collection) o;

            for (Object i : co) {
                value.add(toPropertyValue(i));
            }
            return value;
        } else if (isConfigurable(o.getClass())) {
            FromConfigurationPropertyValue value = null;
            try {
                value = new FromConfigurationPropertyValue(createConfigurationFor(o).loadFrom(o));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationUtils.class.getName()).log(Level.SEVERE, null, ex);
                //cannot happen
            }
            return value;
        } else {
            return new FromStringPropertyValue(o.toString());
        }
    }

    /**
     * read an object configuration from the given file
     *
     * @param <T>
     * @param f
     * @return
     */
    public static Configuration read(File f) throws IOException {
        try {
            Builder builder = new Builder();
            Document doc = builder.build(f);

            return ConfigurationUtils.fromXML(doc.getRootElement());
        } catch (ParsingException ex) {
            throw new IIOException("improper formated file", ex);
        } catch (ClassNotFoundException ex) {
            throw new IIOException("cannot create configuration from given file", ex);
        }
    }

    /**
     * read an object configuration from the given file
     *
     * @param <T>
     * @param in
     * @param f
     * @return
     */
    public static Configuration read(InputStream in) throws IOException {
        try {
            Builder builder = new Builder();
            Document doc = builder.build(in);

            return ConfigurationUtils.fromXML(doc.getRootElement());
        } catch (ParsingException ex) {
            throw new IIOException("improper formated file", ex);
        } catch (ClassNotFoundException ex) {
            throw new IIOException("cannot create configuration from given file", ex);
        }
    }

    /**
     * an attributes of configuration is all the properties which have a type
     * that is not a complex vale (configurable, collection, etc.)
     *
     * @param rconf
     * @return
     */
    public static List<Property> getAttributesOf(Configuration rconf) {
        LinkedList<Property> results = new LinkedList<>();
        for (Property p : rconf) {
            final Class type = p.typeInfo().getType();
            if (!Collection.class.isAssignableFrom(type) && !isConfigurable(type)) {
                results.add(p);
            }
        }

        return results;
    }

}
