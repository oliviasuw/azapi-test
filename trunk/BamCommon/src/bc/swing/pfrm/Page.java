/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.swing.pfrm.params.BaseParamModel;
import bc.dsl.ReflectionDSL;
import bc.swing.pfrm.ano.ViewHints;
import java.util.LinkedHashMap;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.ano.ParamExtractor;
import bc.swing.pfrm.params.ListChangeDeltaHint;
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
    private Map<String, ParamModel> parameters;
    private Class<? extends PageView> viewClass;
    private WeakReference<PageView> view; //if the view is unused throw it automaticly away..
    private List<PageView> disposeList = new LinkedList<PageView>();
    private Model model;

    public Page(String name, ImageIcon icon) {
        this.name = name;

        this.actions = new LinkedList<Action>();
        this.icon = icon;
        this.parameters = new LinkedHashMap<String, ParamModel>();
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
        for (ParamModel p : getParams()) {
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

        for (ParamModel param : getParams()) {
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

    protected void addParam(String name, ParamModel d) {
        parameters.put(name, d);
    }

    public ParamModel getParam(String name) {
        return parameters.get(name);
    }

    public List<ParamModel> getParams() {
        return new LinkedList<ParamModel>(parameters.values());
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
        for (ParamModel p : getParams()) {
            System.out.println("Syncing Parameter: " + p.getName());
            p.syncFromView();
        }
    }

    public void syncParameterFromModel(String param) {
        ParamModel p = parameters.get(param);
        if (p != null) {
            p.fireValueChanged();
        }
    }

    public void syncParameterFromModel(String param, ListChangeDeltaHint hint) {
        ParamModel p = parameters.get(param);
        if (p != null) {
            p.fireValueChanged(hint);
        }
    }

    public void syncParameterFromView(String param) {
        ParamModel p = parameters.get(param);
        if (p != null) {
            p.syncFromView();
        }
    }

    public void syncParametersFromModel() {
        //TODO - do this on set model and here only check the parameters hash
        Field[] fields = model.getClass().getDeclaredFields();
        Param dano;
        ParamExtractor pexano;
        Model inModel = null;

        for (Field field : fields) {
            if (field.isAnnotationPresent(Param.class)) {
                dano = field.getAnnotation(Param.class);
                field.setAccessible(true);
                if (parameters.containsKey(dano.name())) {
                    parameters.get(dano.name()).fireValueChanged();
                } else {
                    try {
                        ParamModel pmod = generateParam(dano, field, model);
                        addParam(dano.name(), pmod);
                    } catch (Exception ex) {
                        Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (field.isAnnotationPresent(ParamExtractor.class)) {
                pexano = field.getAnnotation(ParamExtractor.class);
                for (Param p : pexano.params()) {
                    if (parameters.containsKey(p.name())) {
                        parameters.get(p.name()).fireValueChanged();
                    } else {
                        try {
                            field.setAccessible(true);
                            inModel = (Model) field.get(model);
                            Field inField = readFieldWithParamBase(inModel, p);
                            ParamModel pmod = generateParam(p, inField, inModel);
                            pmod.setVhints(p.vhints());

                            addParam(p.name(), pmod);
                        } catch (Exception ex) {
                            Logger.getLogger(Page.class.getName()).log(Level.SEVERE,
                                    "While Creating Parameter From Within Inner Model - parameter: " + p.name()
                                    + "\ninner model is " + inModel + " and it was taken from the field: " + field.getName(), ex);
                        }
                    }
                }
            }

        }

    }

    private Field readFieldWithParamBase(Model inModel, Param p) throws NoSuchFieldException, SecurityException {
        Field[] fields = inModel.getClass().getDeclaredFields();
        Param pt;
        for (Field f : fields) {
            if (f.isAnnotationPresent(Param.class)) {
                pt = f.getAnnotation(Param.class);
                if (pt.name().equals(p.baseName())) {
                    f.setAccessible(true);
                    return f;
                }
            }
        }

        return null;
    }

    private ParamModel generateParam(Param dano, Field field, Model m) {
        ParamModel pmod = new ParamModel(dano.name(), SwingDSL.resIcon(dano.icon()), dano.type().getViewClass());
        pmod.setPage(this);
        pmod.setModel(m);
        pmod.setField(field);
        pmod.setRole(dano.role());

        if (field.isAnnotationPresent(ViewHints.class)) {
            pmod.setVhints(field.getAnnotation(ViewHints.class));
        } else {
            pmod.setVhints(dano.vhints());
        }


        //ADD ACTIONS
        for (Method mtd : ReflectionDSL.getAllMethodsWithAnnotation(m.getClass(), bc.swing.pfrm.ano.Action.class)) {
            bc.swing.pfrm.ano.Action acano = mtd.getAnnotation(bc.swing.pfrm.ano.Action.class);
            if (acano.forParam().equals(dano.name())) {
                pmod.addAction(createActionFromActionAnnotation(acano, mtd, m));
            }
        }

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
        ret.syncParametersFromModel();
        model.whenPageCreated(ret);

        return ret;

    }

//    private Method readSelectionChangedCallback(Model model, String propName) {
//        Method[] methods = model.getClass().getMethods();
//        bc.swing.pfrm.ano.WhenSelectionChanged deano;
//        for (final Method m : methods) {
//            if (m.isAnnotationPresent(bc.swing.pfrm.ano.WhenSelectionChanged.class)) {
//                deano = m.getAnnotation(bc.swing.pfrm.ano.WhenSelectionChanged.class);
//                if (deano.param().equals(propName)) {
//                    return m;
//                }
//            }
//        }
//
//        return null;
//    }

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
