/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.utils;

import bgu.dcr.az.common.reflections.ReflectionUtils;
import bgu.dcr.az.conf.ConfigurableTypeInfoImpl;
import bgu.dcr.az.conf.api.TypeInfo;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Zovadi
 */
public class JavaTypeParser {
    public static TypeInfo OBJECT_TYPE = parse("java.lang.Object");
    
    public static void main(String[] args) {
        System.out.println(parse("java.lang.String"));
        System.out.println(parse("java.util.List<java.lang.Integer>"));
        System.out.println(parse("java.util.List<T extends java.lang.Integer>"));
        System.out.println(parse("java.util.Map<java.util.List<java.lang.Integer>, java.lang.String>"));
        System.out.println(parse("java.util.Map<java.util.Map<java.util.List<java.lang.Integer>, java.lang.String>, java.util.Map<java.util.List<java.lang.Integer>, java.lang.String>>"));
    }

    public static ConfigurableTypeInfoImpl parse(String str) {
        str = str.replaceAll("[^\\s<>,]+ extends ", "");
        str = str.replaceAll("[^\\s<>,]+ super ", "");
        return parseOuter(str.replaceAll("\\s", ""));
    }

    private static ConfigurableTypeInfoImpl parseOuter(String str) {
        int separator = str.indexOf("<");

        if (separator == -1) {
            str = ReflectionUtils.PRIMITIVE_TO_WRAPPER_CLASS.containsKey(str) ? ReflectionUtils.PRIMITIVE_TO_WRAPPER_CLASS.get(str).getCanonicalName() : str;
            return new ConfigurableTypeInfoImpl(str);
        }
        
        ConfigurableTypeInfoImpl pd = parseOuter(str.substring(0, separator));
        pd.addAll(parseInner(str.substring(separator + 1, str.length() - 1)));
        
        return pd;
    }

    private static Collection<ConfigurableTypeInfoImpl> parseInner(String str) {
        LinkedList<ConfigurableTypeInfoImpl> pds = new LinkedList<>();
        
        while (!str.isEmpty()) {
            int splitter = findNextTypeSeparator(str);
            
            if (splitter == -1) {
                pds.add(parseOuter(str));
                break;
            } else {
                pds.add(parseOuter(str.substring(0, splitter)));
                str = str.substring(splitter + 1);
            }
        }
        
        return pds;
    }

    private static int findNextTypeSeparator(String str) {
        int br = 0;
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '<': br++; break;
                case '>': br--; break;
                case ',': if (br == 0) return i;
            }
        }
        return -1;
    }
}
