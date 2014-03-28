/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf;

import bgu.dcr.az.conf.api.TypeInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class ConfigurableTypeInfoImpl implements TypeInfo {

    private Class clazz;
    private final List<TypeInfo> subtypes;

    public ConfigurableTypeInfoImpl(Class clazz) {
        this.clazz = clazz;
        subtypes = Collections.EMPTY_LIST;
    }

    public ConfigurableTypeInfoImpl(String className) {
        char[] chars = className.toCharArray();
        boolean foundClass = false;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.' && i + 1 < chars.length && Character.isUpperCase(chars[i + 1])) {
                if (foundClass) {
                    chars[i] = '$';
                } else {
                    foundClass = true;
                }
            }
        }

        className = new String(chars);

        try {
            String prefix = "";
            while (className.endsWith("[]")) {
                prefix += "[";
                className = className.substring(0, className.length() - "[]".length());
            }
            if (!prefix.isEmpty()) {
                switch(className) {
                    case "int": className = prefix + "I"; break;
                    case "short": className = prefix + "S"; break;
                    case "long": className = prefix + "J"; break;
                    case "char": className = prefix + "C"; break;
                    case "double": className = prefix + "D"; break;
                    case "float": className = prefix + "F"; break;
                    case "byte": className = prefix + "B"; break;
                    case "boolean": className = prefix + "Z"; break;
                    default: className = prefix + "L" + className; break;
                }
            }
            this.clazz = Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
            this.clazz = Object.class;
        }
        subtypes = new LinkedList<>();
    }

    @Override
    public Class getType() {
        return clazz;
    }

    @Override
    public List<TypeInfo> getGenericParameters() {
        return subtypes;
    }

    public void addGenericParameter(ConfigurableTypeInfoImpl type) {
        subtypes.add(type);
    }

    public void addAll(Collection<ConfigurableTypeInfoImpl> types) {
        subtypes.addAll(types);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(clazz.getCanonicalName());
        if (!subtypes.isEmpty()) {
            sb.append("<");
            for (TypeInfo t : subtypes) {
                sb.append(t.toString()).append(", ");
            }
            sb.delete(sb.length() - ", ".length(), sb.length());
            sb.append(">");
        }
        return sb.toString();
    }

}
