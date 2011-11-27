/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.configurable;

import bc.ui.swing.lists.ComponentList;
import bgu.csp.az.api.infra.VariableMetadata;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
    
    public Map<String, Object> getConfiguration(){
        Map<String, Object> ret = new HashMap<String, Object>();
        for (Entry<Object, JComponent> v : this.items.entrySet()){
            final Object value = ((SingleVariableEditor)v.getValue()).getValue();
            //TODO - IF VALUE IS NULL => provide feedback to the user.
            ret.put(((VariableMetadata)v.getKey()).getName(), value);
        }
        
        return ret;
    }
}
