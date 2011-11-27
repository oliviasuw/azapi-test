/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.swing.pfrm.ano.PageDef;
import bc.dsl.ReflectionDSL;
import java.util.LinkedHashMap;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.ano.Param;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import static bc.dsl.SwingDSL.*;

/**
 *
 * @author BLutati
 */
public class Page implements Model.ParameterChangeListener {

    private String name;
    private ImageIcon icon;
    private List<Action> actions;
    private Map<String, Parameter> parameters;
    private Class<? extends PageLayout> layoutClass;
    private PageLayout view;
    private List<PageLayout> disposeList = new LinkedList<PageLayout>();
    private Model model;

    public Page(String name, ImageIcon icon) {
        this.name = name;
        this.actions = new LinkedList<Action>();
        this.icon = icon;
        this.parameters = new LinkedHashMap<String, Parameter>();
    }

    private static PageDef retrivePageAnnotation(final Model model) throws InvalidParameterException {
        final PageDef pano = model.getClass().getAnnotation(PageDef.class);
        if (pano == null) {
            throw new InvalidParameterException("No Page Annotation is found in class " + model.getClass().getName());
        }
        return pano;
    }

    public static ImageIcon getIconOf(Model value) {
        return pageIcon(retrivePageAnnotation(value).icon());
    }

    public int getParamIndex(String paramName) {
        int i = 0;
        for (Parameter p : getParams()) {
            if (p.getName().equals(paramName)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
        model.setPage(this);
    }

    public Page(String name, ImageIcon icon, Class<? extends PageLayout> viewClass) {
        this(name, icon);
        setViewClass(viewClass);
    }

    public void addToDisposeList(PageLayout who) {
        disposeList.add(who);
    }

    public void disposeView() {
        if (view != null) {
            view.onDispose();
            view = null;
        }

        for (PageLayout d : disposeList) {
            d.onDispose();
        }
        disposeList.clear();
    }

    public void setViewClass(Class<? extends PageLayout> viewClass) {
        this.layoutClass = viewClass;
    }

    protected void addParam(String name, Parameter d) {
        parameters.put(name, d);
    }

    public Parameter param(String name) {
        return parameters.get(name);
    }
    
    public List<Parameter> getParams() {
        return new LinkedList<Parameter>(parameters.values());
    }

    public Parameter firstWithRole(String role){
        for (Parameter p : getParams()) {
            if (p.getRole().equals(role)) {
                return p;
            }
        }

        return null;
    }
    
    public List<Parameter> paramsByRole(String role) {
        LinkedList<Parameter> ret = new LinkedList<Parameter>();
        for (Parameter p : getParams()) {
            if (p.getRole().equals(role)) {
                ret.add(p);
            }
        }
        return ret;
    }

    public JPanel getView() {
        if (view == null && layoutClass != null) {
            try {
                view = layoutClass.newInstance(); //used as anchor do not delete!
            } catch (InstantiationException ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            }
            view.setPage(this);
            model.configurePageLayout((JPanel) view);
        }

        return (JPanel) view;
    }

    private static Action createActionFromActionAnnotation(final bc.swing.pfrm.ano.Action acano, final Method method, final Model x) {
        final Action action = new Action(acano.name(), actionIcon(acano.icon())) {

            @Override
            public void execute() {
                try {
                    method.invoke(x);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Page.class.getName()).log(Level.SEVERE, "ACTION EXECUTION FAILED - " + acano.name(), ex.getCause());
                } catch (Exception ex) {
                    Logger.getLogger(Page.class.getName()).log(Level.SEVERE, "ACTION EXECUTION FAILED - " + acano.name(), ex);
                }
            }
        };

        action.setDefaultAction(acano.defaultAction());
        action.setItemAction(acano.itemAction());
        return action;
    }

    public Page addAction(Action a) {
        actions.add(a);
        return this;
    }

    public List<Action> getActions() {
        return actions;
    }

    public String getName() {
        return name;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public static ImageIcon pageIcon(String name) {
        if (name.isEmpty()) {
            return null;
        }
        return resIcon("resources/img/" + name + ".png");
    }

    public static ImageIcon actionIcon(String name) {
        if (name.isEmpty()) {
            return null;
        }
        return resIcon("resources/img/" + name + ".png");
    }

    /**
     * TODO: REMOVE
     */
    public void syncParametersFromViewToModel() {
        syncParametersFromView();
    }

    public void syncParametersFromView() {
        for (Parameter p : getParams()) {
            System.out.println("Syncing Parameter: " + p.getName());
            p.syncFromView();
        }
    }

    public void syncParameterFromModel(String param) {
        Parameter p = parameters.get(param);
        if (p != null) {
            p.valueChanged();
        }
    }

    public void syncParameterFromModel(String param, DeltaHint hint) {
        Parameter p = parameters.get(param);
        if (p != null) {
            p.fireValueChanged(hint);
        }
    }

    public void syncParameterFromView(String param) {
        Parameter p = parameters.get(param);
        if (p != null) {
            p.syncFromView();
        }
    }

    private void loadParameters(Model model) {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(model.getClass(), Param.class);
        Param dano;

        for (Method f : methods) {
            dano = f.getAnnotation(Param.class);
            try {
                Parameter pmod = generateParam(dano, f.getName().substring(3));
                addParam(dano.name(), pmod);
            } catch (Exception ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * dont use regulary - very heavy update :(
     */
    public void syncParametersFromModel() {
        for (Parameter p : parameters.values()) {
            p.valueChanged();
        }
    }

    private Parameter generateParam(Param dano, String field) {
//        Parameter pmod = new Parameter(dano.name(), SwingDSL.resIcon(dano.icon()), dano.preferedAdapter(), new ReflectionDSL.GetterAndSetter(getModel(), field));
//        pmod.setModel(getModel());
//        pmod.setRole(dano.role());

        //ADD ACTIONS
//        for (Method mtd : ReflectionDSL.getAllMethodsWithAnnotation(getModel().getClass(), bc.swing.pfrm.ano.Action.class)) {
//            bc.swing.pfrm.ano.Action acano = mtd.getAnnotation(bc.swing.pfrm.ano.Action.class);
//            if (acano.forParam().equals(dano.name())) {
//                pmod.addAction(createActionFromActionAnnotation(acano, mtd, getModel()));
//            }
//        }
        return null;
    }

    /**
     * x should be annotated with page annotations
     * @param model
     * @return
     */
    public static Page get(final Model model) {

        if (model.getPage() != null) {
            return model.getPage();
        }

        Page ret = new Page(model.getPageName(), model.getPageIcon(), model.getPageDefaultView());

        //Add Actions
        for (final Method method : ReflectionDSL.getAllMethodsWithAnnotation(model.getClass(), bc.swing.pfrm.ano.Action.class)) {
            bc.swing.pfrm.ano.Action acano = method.getAnnotation(bc.swing.pfrm.ano.Action.class);
            if (acano.forParam().isEmpty()) {
                Action action = createActionFromActionAnnotation(acano, method, model);
                ret.addAction(action);
            }
        }

        ret.setModel(model);
        ret.loadParameters(model);
        model.whenPageCreated(ret);

        return ret;

    }

    public void executeAction(String name) {
        getAction(name).execute();
    }

    public Action getAction(String name) {
        for (Action ac : actions) {
            if (ac.getName().equals(name)) {
                return ac;
            }
        }

        return null;
    }

    public void onChange(Model model, String propertyName, Object hint) {
        parameters.get(propertyName).valueChanged();
    }

    public void setName(String name) {
        this.name = name;
    }
}
