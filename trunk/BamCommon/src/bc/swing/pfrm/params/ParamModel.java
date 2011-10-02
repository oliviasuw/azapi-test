/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params;

import bc.dsl.JavaDSL;
import bc.swing.models.DataExtractor;
import bc.swing.models.DataInserter;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.ViewHints;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 *
 * @author BLutati
 */
public class ParamModel extends BaseParamModel {

   
    private Field field;
    

    public ParamModel(String name, ImageIcon icon, Class<? extends ParamView> view) {
        super(name, icon, view);
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getValue() {
        try {
            return field.get(model);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void setValue(Object value) {
        try {
            if (!JavaDSL.eq(getValue(), value)) {
                field.set(model, value);
                fireValueChanged();
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static interface ChangeListener {

        void onChange(BaseParamModel source, Object newValue, Object deltaHint);
    }
}
