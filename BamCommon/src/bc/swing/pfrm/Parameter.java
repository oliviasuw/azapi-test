/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.dsl.AdapterUtils;
import bc.dsl.GSBox;
import bc.dsl.ReflectionDSL;
import bc.swing.pfrm.scan.BamRegistary;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author BLutati
 */
public class Parameter {

    protected LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();
    protected LinkedList<SelectionListener> selectionListeners = new LinkedList<SelectionListener>();
    protected String name;
    protected ImageIcon icon;
    protected String role;
    protected Model model;
    protected List<Action> actions = new LinkedList<Action>();
    protected ViewAdapter adapter;
    protected String type;
    protected GSBox getset;
    
    public Parameter(String name, ImageIcon icon, String type, GSBox getset) {
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.getset = getset;
    }

    public Object dragFilter(Object dragged) {
        return getModel().dragFilter(getName(), dragged);
    }

    public Object dropFilter(Object dropped) {
        return getModel().dropFilter(getName(), dropped);
    }

    public void addAction(Action a) {
        actions.add(a);
    }

    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public void addSelectionListner(SelectionListener l) {
        selectionListeners.add(l);
    }
    
    public void fireValueChanged(DeltaHint hint) {
        final Object v = getValue();
        for (ChangeListener l : listeners) {
            l.onChangeHappened(this, v, hint);
        }
    }

    public List<Action> getActions() {
        return actions;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }

    public Model getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public Object getValue(){
        return getset.get();
    }

    public boolean isReadOnly(){
        return getset.isReadOnly();
    }
    
    public void setModel(Model model) {
        this.model = model;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setValue(Object value){
        getset.set(value);
    }

    public void executeDefaultAction(){
        Action a = getDefaultAction();
        if (a != null){
            a.execute();
        }
    }

    public Action getDefaultAction() {
        for (Action a : getActions()){
            if (a.isDefaultAction()) return a;
        }

        return null;
    }

    public void syncFromView() {
        if (adapter != null){
            adapter.getIn();
        }
    }

    public void valueChanged() {
        if (adapter != null){
            adapter.syncOut(DeltaHint.noHint());
        }
    }

//    public boolean adapt(Object to, String as) {
//        this.adapter = AdapterUtils.vadapter(type, as);
//        adapter.configure(this.getset, to);
//    }
    
//    public void adapt(Object to) {
//        adapt(to, "DEFAULT");
//    }
}
