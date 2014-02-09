/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.conf.Property;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public class PropertyUtils {

    public static boolean isPrimitive(Property property) {
        Class pType = property.typeInfo().getType();
        return isPrimitive(pType);
    }

    public static boolean isPrimitive(Class pType) {
        return String.class.isAssignableFrom(pType)
                || Integer.class.isAssignableFrom(pType)
                || Boolean.class.isAssignableFrom(pType)
                || Double.class.isAssignableFrom(pType)
                || Float.class.isAssignableFrom(pType)
                || pType.isEnum()
                || Character.class.isAssignableFrom(pType)
                || Byte.class.isAssignableFrom(pType)
                || Short.class.isAssignableFrom(pType)
                || Long.class.isAssignableFrom(pType);

    }
    
    public static boolean isCollection(Class pType) {
        return Collection.class.isAssignableFrom(pType);
    }

    public static boolean isCollection(Property property) {
        return isCollection(property.typeInfo().getType());
    }
}
