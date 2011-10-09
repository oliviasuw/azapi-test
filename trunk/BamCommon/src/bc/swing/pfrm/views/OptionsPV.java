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
import javax.swing.JComboBox;

/**
 *
 * @author bennyl
 */
public class OptionsPV extends JComboBox implements ParamView{

    public void setParam(final BaseParamModel param) {
        SwingDSL.fill(this, param.getValue().getClass());
        this.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                param.fireSelectionChanged(getSelectedItem());
            }
        });
    }

    public void reflectChangesToParam(BaseParamModel to) {
        to.setValue(getSelectedItem());
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
