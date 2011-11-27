/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.adapt;

import bc.dsl.AdapterUtils;
import bc.swing.pfrm.DeltaHint;
import bc.swing.pfrm.ViewAdapter;
import bc.swing.pfrm.Visual2;
import bc.swing.pfrm.scan.Adapt;
import bc.swing.pfrm.scan.Box;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JTabbedPane;

/**
 *
 * @author bennyl
 */
@Adapt(from="LIST ?", to="TABS")
public class ListToTabAdapter implements ViewAdapter<List, JTabbedPane> {
    private List in;
    private JTabbedPane out;
    private Map<String, String> params;
    
    public Object getSelectedItem() {
        return in.get(out.getSelectedIndex());
    }

    public List<Object> getSelectedItems() {
        return Arrays.asList(getSelectedItem());
    }

    public void configure(List in, JTabbedPane out) {
        this.in = in;
        this.out = out;
    }

    public void syncIn() {
        //NOTHING TO DO
    }


    public List getIn() {
        return in;
    }

    public JTabbedPane getOut() {
        return out;
    }

    public void syncOut(DeltaHint deltaHint) {
        out.removeAll();
        for (Object i : getIn()){
            Visual2 v = new Visual2(i);
//            AdapterUtils.adapt(i, v);
        }
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    
}
