/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.comp.JFilePath;
import bc.swing.pfrm.params.BaseParamModel;
import bc.swing.pfrm.params.ParamView;
import java.io.File;

/**
 *
 * @author BLutati
 */
public class FileSelectionPV extends JFilePath implements ParamView {

    public void setParam(BaseParamModel model) {
        Object val = model.getValue();
        if (val != null) {
            File f = (File) val;
            setValueText(f.getAbsolutePath());
        } else {
            setValueText("");
        }
    }

    public void reflectChangesToParam(BaseParamModel to) {
        File f = (File) to.getValue();
        to.setValue(new File(getValueText()));
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        File f = (File) newValue;
        setValueText(f.getAbsolutePath());
    }
}
