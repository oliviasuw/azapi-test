/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.params;

/**
 *
 * @author BLutati
 */
public interface ParamView extends ParamModel.Listener {
    public void setModel(ParamModel model);
    public void reflectChanges(ParamModel to);
}
