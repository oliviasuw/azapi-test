/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.swing.comp.JActionView;
import bc.swing.comp.JCaptionToolbar;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.layouts.PageFrame;
import bc.swing.pfrm.BaseParamModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class PageDSL {

    public static void fillByRole(Page model, JPanel container, String role) {
        container.setLayout(new GridBagLayout());
        container.removeAll();
        List<BaseParamModel> l = model.getParamsWithRole(role);

        if (l.isEmpty()) {
            System.err.println("There Is No Items With role: " + role + " in page " + model.getName());
            return;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = gbc.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        container.add(l.get(0).getDefaultView(), gbc);
//        insertToCenterByRole(model, container, role);
    }

    public static void fillByRole(Page model, JPanel container, String role, int insets) {
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

    public static void insertToCenterByRole(Page model, JPanel container, String role) {
        List<BaseParamModel> l = model.getParamsWithRole(role);
        container.add(l.get(0).getDefaultView(), BorderLayout.CENTER);
    }

    public static JComponent viewByRole(Page page, String role) {
        final List<BaseParamModel> paramsWithRole = page.getParamsWithRole(role);
        final BaseParamModel p = (paramsWithRole.isEmpty() ? null : paramsWithRole.get(0));
        return (p == null ? null : p.getDefaultView());
    }

    public static void showInFrame(Model model) {
        PageFrame.show(Page.get(model));
    }
    
    public static void showInNonClosingFrame(Model model) {
        PageFrame.showDontExit(Page.get(model));
    }
    

    public static void appendActions(List<Action> actions, JPanel pan) {
        for (Action a : actions) {
            pan.add(new JActionView(a));
        }
    }
}
