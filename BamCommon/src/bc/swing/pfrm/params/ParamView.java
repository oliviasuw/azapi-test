/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.params;

/**
 *
 * @author BLutati
 */
public interface ParamView extends ParamModel.ChangeListener {
    public void setParam(BaseParamModel param);
    public void reflectChangesToParam(BaseParamModel to);
}
