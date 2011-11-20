/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bc.dsl.PageDSL;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.viewtypes.ParamType;
import bgu.csp.az.dev.pui.AZView;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultBoundedRangeModel;

/**
 *
 * @author bennyl
 */
@PageDef(layout=AZView.class)
public class NewUIController extends Model{
    
    @Param(name = "Execution Progress", type = ParamType.PROGRESS, role = AZView.PROGRESS_BAR_ROLE)
    DefaultBoundedRangeModel progress = new DefaultBoundedRangeModel(5, 1, 0, 100);
    
    @Param(name="Pages", type= ParamType.TABS, role=AZView.PAGES_ROLE)
    public List<Model> getPages(){
        return new LinkedList<Model>();
    }
    
    @Action(name=AZView.STOP_AND_SAVE_ACTION)
    public void stopAndSave(){
        
    }
    
    public static void main(String[] args){
        SwingDSL.configureUI();
        PageDSL.showInFrame(new NewUIController());
    }
}
