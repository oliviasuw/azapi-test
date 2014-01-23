/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ReflectionUtils {

    private static final ConcurrentHashMap<Class, Method> valueOfCache = new ConcurrentHashMap<>();
    private static final Class[] KNOWN_COLLECTION_TYPES = {
        LinkedList.class,
        ArrayList.class,
        HashSet.class
    };

    public static final Map<String, Class> PRIMITIVE_TO_WRAPPER_CLASS = new HashMap<String, Class>(){{
        put("boolean", Boolean.class);
        put("byte", Byte.class);
        put("short", Short.class);
        put("char", Character.class);
        put("int", Integer.class);
        put("long", Long.class);
        put("float", Float.class);
        put("double", Double.class);
    }};
    
    public static <T> T valueOf(String s, Class<T> c) throws NoSuchMethodException {
        if (c == String.class) return (T) s;
        
        Method vof = valueOfCache.get(c);
        if (vof == null) {
            vof = c.getMethod("valueOf", String.class);

            vof.setAccessible(true);

            if (!Modifier.isStatic(vof.getModifiers())) {
                throw new NoSuchMethodException("valueOf method must be static but no such found in class: " + c.getCanonicalName());
            }

            if (!c.isAssignableFrom(vof.getReturnType())) {
                throw new NoSuchMethodException("valueOf method must return a value that is assignable to the declaring class but no such found in class: " + c.getCanonicalName());
            }

            valueOfCache.put(c, vof);
        }

        try {
            return (T) vof.invoke(null, s);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            //should never happened.
            return null;
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * attempt to create a collection that can be assignable to the given class
     *
     * @param type
     * @return
     * @throws UnsupportedOperationException
     */
    public static Collection createSuitableCollection(Class type) throws UnsupportedOperationException {
        if (Collection.class.isAssignableFrom(type)) {
            //Collection c = ReflectionUtils.createSuitableCollection(type);

            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                for (Class k : KNOWN_COLLECTION_TYPES) {
                    if (type.isAssignableFrom(k)) {
                        type = k;
                        break;
                    }
                }
            }

            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                throw new UnsupportedOperationException(String.format("dosent know how to create a collection type for class %s", type.getCanonicalName()));
            }

            ConstructorAccess access = ConstructorAccess.get(type);
            try {
                return (Collection) access.newInstance();
            } catch (Exception ex) {
                throw new UnsupportedOperationException(String.format("cannot create collection for class: %s", type), ex);
            }
        } else {
            throw new UnsupportedOperationException(String.format("type: %s is not a collection.", type.getCanonicalName()));
        }
    }
}
