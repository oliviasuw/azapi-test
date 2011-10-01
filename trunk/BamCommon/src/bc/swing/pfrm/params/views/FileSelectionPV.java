/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.comp.JFilePath;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;
import java.io.File;

/**
 *
 * @author BLutati
 */
public class FileSelectionPV extends JFilePath implements ParamView {

    public void setModel(ParamModel model) {
        Object val = model.getValue();
        if (val != null) {
            File f = (File) val;
            setValueText(f.getAbsolutePath());
        } else {
            setValueText("");
        }
    }

    public void reflectChanges(ParamModel to) {
        File f = (File) to.getValue();
        to.setValue(new File(getValueText()));
    }

    public void onChange(ParamModel source, Object newValue, Object deltaHint) {
        File f = (File) newValue;
        setValueText(f.getAbsolutePath());
    }
}
