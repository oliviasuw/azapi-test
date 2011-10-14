/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.dsl.ReflectionDSL;
import bc.swing.models.DataExtractor;
import bc.swing.models.DataInserter;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.FieldParamModel.ChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 *
 * @author BLutati
 */
public abstract class BaseParamModel {

    protected LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();
    protected LinkedList<ChangeListener> selectionListeners = new LinkedList<ChangeListener>();
    protected String name;
    protected ImageIcon icon;
    protected Class<? extends ParamView> viewClass;
    protected ParamView view;
    protected String role;
    protected Page page;
    protected Model model;
    protected ViewHints vhints;
    protected List<Action> actions = new LinkedList<Action>();
    protected Object selectedItem = null;
    protected int number = 0;
    
    public BaseParamModel(String name, ImageIcon icon, Class<? extends ParamView> view) {
        this.name = name;
        this.icon = icon;
        this.viewClass = view;
        addSelectionListner(new ChangeListener() {

            public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
                selectedItem = newValue;
            }
        });
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
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

    public void addSelectionListner(ChangeListener l) {
        selectionListeners.add(l);
    }

    public void disposeView() {
        view = null;
    }

    public void fireSelectionChanged(Object newSelection) {
        for (ChangeListener sl : selectionListeners) {
            sl.onChange(this, newSelection, null);
        }
    }

    public void fireValueChanged() {
        fireValueChanged(null);
    }

    public void fireValueChanged(Object hint) {
        final Object v = getValue();
        for (ChangeListener l : listeners) {
            l.onChange(this, v, hint);
        }
    }

    public List<Action> getActions() {
        return actions;
    }

    public DataExtractor getDataExtractor() {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getPage().getModel().getClass(), bc.swing.pfrm.ano.DataExtractor.class);
        bc.swing.pfrm.ano.DataExtractor deano;
        for (final Method m : methods) {
            if (m.isAnnotationPresent(bc.swing.pfrm.ano.DataExtractor.class)) {
                deano = m.getAnnotation(bc.swing.pfrm.ano.DataExtractor.class);
                return new DataExtractor(deano.columns()) {

                    @Override
                    public Object getData(String dataName, Object from) {
                        try {
                            return m.invoke(getPage().getModel(), dataName, from);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return "ERROR RETRIVING DATA";
                    }
                };
            }
        }
        return null;
    }

    public DataInserter getDataInserter() {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getPage().getModel().getClass(), bc.swing.pfrm.ano.DataInserter.class);
        bc.swing.pfrm.ano.DataInserter deano;
        for (final Method m : methods) {
            deano = m.getAnnotation(bc.swing.pfrm.ano.DataInserter.class);
            if (deano.param().equals(getName())) {
                return new DataInserter(deano.columns()) {

                    @Override
                    public void setData(String dataName, Object from, Object newValue) {
                        try {
                            m.invoke(getPage().getModel(), dataName, from, newValue);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
            }
        }
        return null;
    }

    public JComponent getDefaultView() {
        try {
            if (view == null) {
                view = viewClass.newInstance();
                view.setParam(this);
                addChangeListener(view);
            }
            return (JComponent) view;
        } catch (InstantiationException ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FieldParamModel.class.getName()).log(Level.SEVERE, "when creating default view for " + getPage().getModel().getClass().getSimpleName() + "->" + getName(), ex);
        }
        return null;
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

    public Page getPage() {
        return page;
    }

    public String getRole() {
        return role;
    }

    public Object getSelectedItem() {
        return this.selectedItem;
    }

    public abstract Object getValue();

    public ViewHints getViewHints() {
        return vhints;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public abstract void setValue(Object value);

    public void setVhints(ViewHints vhints) {
        this.vhints = vhints;
    }

    public void syncFromView() {
        view.reflectChangesToParam(this);
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
}
