/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.dsl.ReflectionDSL;
import bc.swing.pfrm.ano.ViewHints;
import java.util.LinkedHashMap;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.ano.Param;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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
    private Map<String, BaseParamModel> parameters;
    private Class<? extends PageView> viewClass;
    private WeakReference<PageView> view; //if the view is unused throw it automaticly away..
    private List<PageView> disposeList = new LinkedList<PageView>();
    private Model model;

    public Page(String name, ImageIcon icon) {
        this.name = name;

        this.actions = new LinkedList<Action>();
        this.icon = icon;
        this.parameters = new LinkedHashMap<String, BaseParamModel>();
    }

    private static bc.swing.pfrm.ano.PageDef retrivePageAnnotation(final Model model) throws InvalidParameterException {
        final bc.swing.pfrm.ano.PageDef pano = model.getClass().getAnnotation(bc.swing.pfrm.ano.PageDef.class);
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
        for (BaseParamModel p : getParams()) {
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
        if (this.model != null) {
            this.model.removeParameterChangedListener(this);
        }

        this.model = model;
        model.addParameterChangedListener(this);
        model.setPage(this);
    }

    public Page(String name, ImageIcon icon, Class<? extends PageView> viewClass) {
        this(name, icon);
        setViewClass(viewClass);
    }

    public void addToDisposeList(PageView who) {
        disposeList.add(who);
    }

    public void disposeView() {
        if (view != null) {
            PageView got = view.get();
            if (got != null) {
                view.get().onDispose();
            }
            view = null;
        }

        for (BaseParamModel param : getParams()) {
            param.disposeView();
        }

        for (PageView d : disposeList) {
            d.onDispose();
        }
        disposeList.clear();
    }

    public void setViewClass(Class<? extends PageView> viewClass) {
        this.viewClass = viewClass;
    }

    protected void addParam(String name, BaseParamModel d) {
        parameters.put(name, d);
    }

    public BaseParamModel getParam(String name) {
        return parameters.get(name);
    }

    public List<BaseParamModel> getParams() {
        return new LinkedList<BaseParamModel>(parameters.values());
    }

    public List<BaseParamModel> getParamsWithRole(String role) {
        LinkedList<BaseParamModel> ret = new LinkedList<BaseParamModel>();
        for (BaseParamModel p : getParams()) {
            if (p.getRole().equals(role)) {
                ret.add(p);
            }
        }
        return ret;
    }

    public JPanel getView() {
        if (view == null || view.get() == null && viewClass != null) {
            try {
                final PageView temp = viewClass.newInstance(); //used as anchor do not delete!
                view = new WeakReference<PageView>(temp);
            } catch (InstantiationException ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            }
            view.get().setPage(this);
        }

        return (JPanel) view.get();
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
        for (BaseParamModel p : getParams()) {
            System.out.println("Syncing Parameter: " + p.getName());
            p.syncFromView();
        }
    }

    public void syncParameterFromModel(String param) {
        BaseParamModel p = parameters.get(param);
        if (p != null) {
            p.fireValueChanged();
        }
    }

    public void syncParameterFromModel(String param, DeltaHint hint) {
        BaseParamModel p = parameters.get(param);
        if (p != null) {
            p.fireValueChanged(hint);
        }
    }

    public void syncParameterFromView(String param) {
        BaseParamModel p = parameters.get(param);
        if (p != null) {
            p.syncFromView();
        }
    }

    private void loadParameters(Model model) {
        List<Field> fields = ReflectionDSL.getAllFieldsWithAnnotation(model.getClass(), Param.class);
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(model.getClass(), Param.class);
        Param dano;

        for (Field f : fields) {
            dano = f.getAnnotation(Param.class);
            try {
                BaseParamModel pmod = generateParam(dano, f, model);
                addParam(dano.name(), pmod);
            } catch (Exception ex) {
                Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (Method f : methods) {
            dano = f.getAnnotation(Param.class);
            try {
                BaseParamModel pmod = generateParam(dano, f, model);
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
        for (BaseParamModel p : parameters.values()){
            p.fireValueChanged();
        }
    }

    private void configureParamModel(BaseParamModel pmod, Model m, Param dano, ViewHints vh) {
        pmod.setPage(this);
        pmod.setModel(m);
        pmod.setRole(dano.role());
        pmod.setVhints(vh);

        //ADD ACTIONS
        for (Method mtd : ReflectionDSL.getAllMethodsWithAnnotation(m.getClass(), bc.swing.pfrm.ano.Action.class)) {
            bc.swing.pfrm.ano.Action acano = mtd.getAnnotation(bc.swing.pfrm.ano.Action.class);
            if (acano.forParam().equals(dano.name())) {
                pmod.addAction(createActionFromActionAnnotation(acano, mtd, m));
            }
        }
    }

    private BaseParamModel generateParam(Param dano, Field field, Model m) {
        FieldParamModel pmod = new FieldParamModel(dano.name(), SwingDSL.resIcon(dano.icon()), dano.type().getViewClass());
        pmod.setField(field);

        ViewHints vh = null;
        if (field.isAnnotationPresent(ViewHints.class)) {
            vh = field.getAnnotation(ViewHints.class);
        } else {
            vh = dano.vhints();
        }

        configureParamModel(pmod, m, dano, vh);
        return pmod;
    }

    private BaseParamModel generateParam(Param dano, Method mtd, Model m) {
        MethodParamModel pmod = new MethodParamModel(dano.name(), SwingDSL.resIcon(dano.icon()), dano.type().getViewClass());
        Method mtds = null;
        if (pmod.getName().startsWith("get")) {
            String setterName = "set" + pmod.getName().substring("get".length());
            mtds = ReflectionDSL.methodByName(m.getClass(), setterName);
        }

        pmod.setGetter(mtd);
        pmod.setSetter(mtds);

        ViewHints vh = null;
        if (mtd.isAnnotationPresent(ViewHints.class)) {
            vh = mtd.getAnnotation(ViewHints.class);
        } else {
            vh = dano.vhints();
        }

        configureParamModel(pmod, m, dano, vh);
        return pmod;
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
        parameters.get(propertyName).fireValueChanged();
    }

    public void setName(String name) {
        this.name = name;
    }
}
