/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.params;

import bc.swing.pfrm.params.views.ConsolePV;
import bc.swing.pfrm.params.views.CustomPV;
import bc.swing.pfrm.params.views.FileSelectionPV;
import bc.swing.pfrm.params.views.FileTreeWithScrollbarPV;
import bc.swing.pfrm.params.views.IPAddressPV;
import bc.swing.pfrm.params.views.InfoPV;
import bc.swing.pfrm.params.views.LabelPV;
import bc.swing.pfrm.params.views.ListPV;
import bc.swing.pfrm.params.views.MapPV;
import bc.swing.pfrm.params.views.PagePV;
import bc.swing.pfrm.params.views.ProgressPV;
import bc.swing.pfrm.params.views.TabsPV;
import bc.swing.pfrm.params.views.StringPV;
import bc.swing.pfrm.params.views.StringWithTitlePV;
import bc.swing.pfrm.params.views.TablePV;
import bc.swing.pfrm.params.views.TreePV;
import bc.swing.pfrm.params.views.XObjectPV;

/**
 *
 * @author BLutati
 */
public enum ParamType {
    TREE(TreePV.class),
    LABEL(LabelPV.class),
    PROGRESS(ProgressPV.class),
    FILE_SELECTION(FileSelectionPV.class),
    MAP(MapPV.class),
    CONSOLE(ConsolePV.class),
    TABS(TabsPV.class),
    STRING_WITH_TITLE(StringWithTitlePV.class),
    XOBJECT(XObjectPV.class),
    LIST(ListPV.class),
    INFO(InfoPV.class),
    TABLE(TablePV.class),
    CUSTOM(CustomPV.class),
    PAGE(PagePV.class),
    IP (IPAddressPV.class),
    STRING (StringPV.class),
    FILE_TREE(FileTreeWithScrollbarPV.class);

    Class<? extends ParamView> view;

    private ParamType(Class<? extends ParamView> view) {
        this.view = view;
    }

    public Class<? extends ParamView> getViewClass() {
        return view;
    }

}
