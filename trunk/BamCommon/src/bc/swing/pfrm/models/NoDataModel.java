/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.models;

import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.layouts.CenterLayout;
import bc.swing.pfrm.viewtypes.ParamType;

/**
 *
 * @author bennyl
 */
@PageDef(layout=CenterLayout.class)
public class NoDataModel extends Model{
    String noDataNotification;

    public NoDataModel(String noDataNotification) {
        this.noDataNotification = noDataNotification;
    }

    public NoDataModel() {
        noDataNotification = "No Data To Show.";
    }
    
    

    @Param(type= ParamType.LABEL, name="No Data Notification", icon="null")
    @ViewHints(horizontalAlignment= ViewHints.Alignment.CENTER)
    public String getNoDataNotification() {
        return noDataNotification;
    }

    public void setNoDataNotification(String noDataNotification) {
        this.noDataNotification = noDataNotification;
    }
    
}
