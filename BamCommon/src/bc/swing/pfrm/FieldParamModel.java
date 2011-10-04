/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.dsl.JavaDSL;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author BLutati
 */
public class FieldParamModel extends BaseParamModel {

   
    private Field field;
    

    public FieldParamModel(String name, ImageIcon icon, Class<? extends ParamView> view) {
        super(name, icon, view);
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getValue() {
        try {
            return field.get(model);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static interface ChangeListener {

        void onChange(BaseParamModel source, Object newValue, Object deltaHint);
    }
}
