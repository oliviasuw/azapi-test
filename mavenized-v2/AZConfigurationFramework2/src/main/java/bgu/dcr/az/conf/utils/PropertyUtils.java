/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.utils;

import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.registery.Registery;
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
        return !Collection.class.isAssignableFrom(pType) && Registery.get().getImplementors(pType).isEmpty();
    }

    public static boolean isCollection(Class pType) {
        return Collection.class.isAssignableFrom(pType);
    }

    public static boolean isCollection(Property property) {
        return isCollection(property.typeInfo().getType());
    }
}
