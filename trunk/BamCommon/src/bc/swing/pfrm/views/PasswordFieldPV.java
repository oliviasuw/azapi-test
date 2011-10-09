/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.ParamView;
import javax.swing.JPasswordField;

/**
 *
 * @author bennyl
 */
public class PasswordFieldPV extends JPasswordField implements ParamView{

    public void setParam(BaseParamModel param) {
        String v = ""+param.getValue();
        setText(v);
    }

    public void reflectChangesToParam(BaseParamModel to) {
        to.setValue(new String(getPassword()));
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        setText("" + newValue);
    }
    
}
