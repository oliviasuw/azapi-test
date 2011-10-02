/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.swing.pfrm.ano.Action;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class ReflectionDSL {

    public static Method methodWithAnnotation(Class c, Class<? extends Annotation> a) {
        for (Method m : c.getDeclaredMethods()) {

            if (m.isAnnotationPresent(a)) {
                m.setAccessible(true);
                return m;
            }
        }

        return null;
    }

    public static List<Method> getAllMethodsWithAnnotation(Class aClass, Class<? extends Annotation> ano) {
        LinkedList<Method> ret = new LinkedList<Method>();
        for (Method m : aClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(ano)) {
                m.setAccessible(true);
                ret.add(m);
            }
        }

        return ret;
    }
}
