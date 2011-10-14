/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.dsl.SwingDSL;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.ParamView;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JComboBox;

/**
 *
 * @author bennyl
 */
public class OptionsPV extends JComboBox implements ParamView {

    ModelTypes mt;

    public void setParam(final BaseParamModel param) {
        testModelType(param);

        switch (mt) {
            case ENUM:
                setEnumParam(param);
                break;
            case LIST:
                setListParam(param);
                break;
        }

        this.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                param.fireSelectionChanged(getSelectedItem());
            }
        });
        
        param.fireSelectionChanged(getSelectedItem());
    }

    private void setEnumParam(final BaseParamModel param) {
        SwingDSL.fill(this, param.getValue().getClass());
    }

    private void setListParam(BaseParamModel param) {
        SwingDSL.fill(this, (List) param.getValue());
    }

    private void testModelType(final BaseParamModel param) {
        if (param.getValue() instanceof Enum) {
            mt = ModelTypes.ENUM;
        } else {
            mt = ModelTypes.LIST;
        }
    }

    public void reflectChangesToParam(BaseParamModel to) {
        switch (mt) {
            case ENUM:
                to.setValue(getSelectedItem());
                break;
            case LIST:
                //DO NOTHING...
                break;
        }
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static enum ModelTypes {

        ENUM,
        LIST;
    }
}
