/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.viewtypes;

import bc.swing.pfrm.ParamView;
import bc.swing.pfrm.views.ChartPV;
import bc.swing.pfrm.views.ConsolePV;
import bc.swing.pfrm.views.CustomPV;
import bc.swing.pfrm.views.DateAndTimePV;
import bc.swing.pfrm.views.FileSelectionPV;
import bc.swing.pfrm.views.TreeWithScrollbarPV;
import bc.swing.pfrm.views.IPAddressPV;
import bc.swing.pfrm.views.InfoPV;
import bc.swing.pfrm.views.LabelPV;
import bc.swing.pfrm.views.ListPV;
import bc.swing.pfrm.views.MapPV;
import bc.swing.pfrm.views.NumberRangePV;
import bc.swing.pfrm.views.OptionsPV;
import bc.swing.pfrm.views.PagePV;
import bc.swing.pfrm.views.PasswordFieldPV;
import bc.swing.pfrm.views.PortletsPV;
import bc.swing.pfrm.views.ProgressPV;
import bc.swing.pfrm.views.RadioGroupPV;
import bc.swing.pfrm.views.TabsPV;
import bc.swing.pfrm.views.StringPV;
import bc.swing.pfrm.views.StringWithTitlePV;
import bc.swing.pfrm.views.TablePV;
import bc.swing.pfrm.views.XObjectPV;

/**
 *
 * @author BLutati
 */
public enum ParamType {
    RADIO_GROUP(RadioGroupPV.class),
    NUMMERIC_RANGE(NumberRangePV.class),
    OPTIONS(OptionsPV.class),
    DATE_AND_TIME(DateAndTimePV.class),
    PASSWORD(PasswordFieldPV.class),
    PORTLETS(PortletsPV.class),
    CHART(ChartPV.class),
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
    TREE(TreeWithScrollbarPV.class);

    Class<? extends ParamView> view;

    private ParamType(Class<? extends ParamView> view) {
        this.view = view;
    }

    public Class<? extends ParamView> getViewClass() {
        return view;
    }

}
