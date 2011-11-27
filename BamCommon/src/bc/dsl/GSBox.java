/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.swing.pfrm.scan.Box;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import static bc.dsl.ReflectionDSL.*;
/**
 *
 * @author bennyl
 */
public class GSBox<T> implements Box<T> {
        Method getter;
        Method setter;
        Object obj;

        public GSBox(Object obj, String field) {
            this.getter = methodByNameAndNArgs(obj.getClass(), "get" + JavaDSL.camelCase(field), 0);
            this.setter = methodByNameAndArgs(obj.getClass(), "set" + JavaDSL.camelCase(field), getter.getReturnType());
            this.obj = obj;
        }
        
        public T get(){
            try {
                return (T) getter.invoke(obj);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return null;
        }
        
        public boolean set(T val){
            if (setter != null){
                try {
                    setter.invoke(obj, val);
                    return true;
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return false;
        }
        
        public boolean isReadOnly(){
            return setter != null;
        }
        
        public Class getType(){
            return getter.getReturnType();
        }
    
}
