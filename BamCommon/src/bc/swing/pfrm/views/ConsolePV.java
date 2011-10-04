/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.swing.comp.Console;
import bc.swing.models.ConsoleModel;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.PageView;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.FieldParamModel;
import bc.swing.pfrm.ParamView;

/**
 *
 * @author bennyl
 */
public class ConsolePV extends Console implements ParamView, PageView {

    public void setParam(BaseParamModel model) {
        ConsoleModel comodel = (ConsoleModel) model.getValue();
        setModel(comodel);
    }

    public void reflectChangesToParam(BaseParamModel to) {
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
    }

    public void setPage(Page page) {
        ConsoleModel comodel = (ConsoleModel) page.getModel();
        setModel(comodel);
    }

    public void onDispose() {
    }


}
