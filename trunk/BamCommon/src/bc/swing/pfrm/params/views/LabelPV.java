/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;
import javax.swing.JLabel;

/**
 *
 * @author bennyl
 */
public class LabelPV extends JLabel implements ParamView{

    public void setModel(ParamModel model) {
        onChange(model, model.getValue(), null);
    }

    public void reflectChanges(ParamModel to) {
        to.setValue(getText());
    }

    public void onChange(ParamModel model, Object newValue, Object deltaHint) {
        String data = (String) newValue;
        setText(data);
        setIcon(model.getIcon());
    }
    
}
