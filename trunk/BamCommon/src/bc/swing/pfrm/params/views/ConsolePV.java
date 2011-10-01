/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.comp.Console;
import bc.swing.models.ConsoleModel;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;

/**
 *
 * @author bennyl
 */
public class ConsolePV extends Console implements ParamView {

    public void setModel(ParamModel model) {
        ConsoleModel comodel = (ConsoleModel) model.getValue();
        setModel(comodel);
    }

    public void reflectChanges(ParamModel to) {
    }

    public void onChange(ParamModel source, Object newValue, Object deltaHint) {
    }
}
