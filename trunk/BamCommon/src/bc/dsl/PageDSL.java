/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.layouts.PageFrame;
import bc.swing.pfrm.BaseParamModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class PageDSL {
    public static void fillByRole(Page model, JPanel container, String role){
        List<BaseParamModel> l = model.getParamsWithRole(role);
        container.setLayout(new BorderLayout());
        container.removeAll();
        container.add(l.get(0).getDefaultView(), BorderLayout.CENTER);
    }
    
    public static void fillByRole(Page model, JPanel container, String role, int insets){
        List<BaseParamModel> l = model.getParamsWithRole(role);
        container.setLayout(new GridBagLayout());
        container.removeAll();
        GridBagConstraints con = new GridBagConstraints();
        con.fill = GridBagConstraints.BOTH;
        con.insets = new Insets(insets, insets, insets, insets);
        con.weightx = 1;
        con.weighty = 1;
        container.add(l.get(0).getDefaultView(), con);
    }
    
    public static void showInFrame(Model model){
        PageFrame.show(Page.get(model));
    }
}
