/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.configurable;

import bc.ui.swing.lists.ComponentList;
import bgu.csp.az.api.infra.VariableMetadata;
import javax.swing.JComponent;

/**
 *
 * @author bennyl
 */
public class VariablesEditor extends ComponentList{

    @Override
    public JComponent createComponentFor(Object item) {
        final SingleVariableEditor sv = new SingleVariableEditor();
        sv.setModel((VariableMetadata)item);
        return sv;
    }
    
    public void setModel(VariableMetadata[] vars){
        clear();
        for (VariableMetadata v : vars){
            add(v);
        }
        
        revalidate();
        repaint();
    }
}
