/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * if configuration is added on a function that is called 
 * add*(item i)
 * then the configuration will be counted as a list of item
 * to read the configuration we assume that there is a twin function named 
 * get*s() that return List<item>
 * 
 * if configuration is added on a function that is called 
 * set*(item i)
 * then the configuration will be counted as a single item
 * to read the configuration we assume that there is a twin function named
 * get*() that return item
 * 
 * @author bennyl
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Configuration {

    String name();

    String description();
}
