/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author bennyl
 */
public class MethodParamModel extends BaseParamModel {

    Method getter = null;
    Method setter = null;

    public MethodParamModel(String name, ImageIcon icon, Class<? extends ParamView> view) {
        super(name, icon, view);
    }

    public void setGetter(Method getter) {
        this.getter = getter;
        if (getter != null) {
            getter.setAccessible(true);
        }
    }

    public void setSetter(Method setter) {
        this.setter = setter;
        if (setter != null) {
            setter.setAccessible(true);
        }
    }

    @Override
    public Object getValue() {
        if (getter != null) {
            try {
                return getter.invoke(getPage().getModel());
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (setter != null) {
            try {
                setter.invoke(model, value);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(MethodParamModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
