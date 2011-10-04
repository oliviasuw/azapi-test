/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm;

/**
 *
 * @author BLutati
 */
public interface ParamView extends FieldParamModel.ChangeListener {
    public void setParam(BaseParamModel param);
    public void reflectChangesToParam(BaseParamModel to);
}
