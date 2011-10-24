/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class MethodNode extends Node {

    Method getter;
    Method setter;

    public MethodNode(Method getter, Method setter, Object target, Node parent) {
        super(parent, target);
        if (getter != null) {
            getter.setAccessible(true);
        }

        if (setter != null) {
            setter.setAccessible(true);
        }

        this.getter = getter;
        this.setter = setter;
        
        putAtt(Att.VALUE_CLASS, getter.getReturnType());
    }

    @Override
    public Object getValue() {
        if (getter != null){
            try {
                return getter.invoke(getTarget());
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }

    @Override
    protected void _setValue(Object value) {
        if (setter != null){
            try {
                setter.invoke(getTarget(), value);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(MethodNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
