/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params;

import bc.dsl.JavaDSL;
import bc.swing.models.DataExtractor;
import bc.swing.models.DataInserter;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.ViewHints;
import java.lang.reflect.Field;
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
public class ParamModel {

    private LinkedList<Listener> listeners = new LinkedList<Listener>();
    private LinkedList<Listener> selectionListeners = new LinkedList<Listener>();
    private Field field;
    private String name;
    private ImageIcon icon;
    private Class<? extends ParamView> viewClass;
    private ParamView view;
    private String role;
    private Page page;
    private Model model;
    private ViewHints vhints;
    private List<Action> actions = new LinkedList<Action>();
    private Object selectedItem = null;

    public ParamModel(String name, ImageIcon icon, Class<? extends ParamView> view) {
        this.name = name;
        this.icon = icon;
        this.viewClass = view;
        addSelectionListner(new Listener() {

            public void onChange(ParamModel source, Object newValue, Object deltaHint) {
                selectedItem = newValue;
            }
        });
    }

    public void addAction(Action a) {
        actions.add(a);
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setVhints(ViewHints vhints) {
        this.vhints = vhints;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void addSelectionListner(Listener l) {
        selectionListeners.add(l);
    }

    public void fireSelectionChanged(Object newSelection) {
        for (Listener sl : selectionListeners) {
            sl.onChange(this, newSelection, null);
        }
    }

    public void disposeView() {
        view = null;
    }

    public JComponent getDefaultView() {
        try {
            if (view == null) {
                view = viewClass.newInstance();
                view.setModel(this);
                addChangeListener(view);
            }

            return (JComponent) view;
        } catch (InstantiationException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex){
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, "when creating default view for " + getPage().getModel().getClass().getSimpleName() + "->" + getName(), ex);
        }

        return null;
    }

    public void syncFromView() {
        view.reflectChanges(this);
    }

    public Object getValue() {
        try {
            return field.get(model);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void setValue(Object value) {
        try {
            if (!JavaDSL.eq(getValue(), value)) {
                field.set(model, value);
                fireValueChanged();
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void addChangeListener(Listener l) {
        listeners.add(l);
    }

    public void fireValueChanged() {
        fireValueChanged(null);
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    public Model getModel() {
        return model;
    }

    public DataExtractor getDataExtractor() {
        Method[] methods = getPage().getModel().getClass().getMethods();
        bc.swing.pfrm.ano.DataExtractor deano;
        for (final Method m : methods) {
            if (m.isAnnotationPresent(bc.swing.pfrm.ano.DataExtractor.class)) {
                deano = m.getAnnotation(bc.swing.pfrm.ano.DataExtractor.class);
                if (deano.param().equals(getName())) {
                    return new DataExtractor(deano.columns()) {

                        @Override
                        public Object getData(String dataName, Object from) {
                            try {
                                return m.invoke(getPage().getModel(), dataName, from);
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            return "ERROR RETRIVING DATA";
                        }
                    };
                }
            }
        }

        return null;
    }

    public DataInserter getDataInserter() {
        Method[] methods = getPage().getModel().getClass().getMethods();
        bc.swing.pfrm.ano.DataInserter deano;
        for (final Method m : methods) {
            if (m.isAnnotationPresent(bc.swing.pfrm.ano.DataInserter.class)) {
                deano = m.getAnnotation(bc.swing.pfrm.ano.DataInserter.class);
                if (deano.param().equals(getName())) {
                    return new DataInserter(deano.columns()) {

                        @Override
                        public void setData(String dataName, Object from, Object newValue) {
                            try {
                                m.invoke(getPage().getModel(), dataName, from, newValue);
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(ParamModel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                }
            }
        }

        return null;
    }

    public ViewHints getViewHints() {
        return vhints;
    }

    public void fireValueChanged(Object hint) {
        final Object v = getValue();
        for (Listener l : listeners) {
            l.onChange(this, v, hint);
        }
    }

    public Object getSelectedItem() {
        return this.selectedItem;
    }

    public static interface Listener {

        void onChange(ParamModel source, Object newValue, Object deltaHint);
    }
}
