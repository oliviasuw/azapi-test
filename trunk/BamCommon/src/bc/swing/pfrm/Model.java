/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.dsl.ReflectionDSL;
import bc.swing.pfrm.ano.OnEvent;
import bc.swing.pfrm.ano.PageDef.DefaultPageView;
import bc.swing.pfrm.events.Event;
import bc.swing.pfrm.events.EventListener;
import bc.swing.pfrm.units.EventBusUnit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.jfree.layout.CenterLayout;

/**
 *
 * @author bennyl
 */
public class Model {

    private Page page;

    public Model() {
        registerToEvents();
    }

    private void registerToEvents() {
        List<Method> emtds = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), OnEvent.class);

        for (final Method mtd : emtds) {
            final OnEvent dano = mtd.getAnnotation(OnEvent.class);
            EventBusUnit.UNIT.register(dano.name(), new EventListener() {

                public void onEvent(Event e) {
                    try {
                        if (dano.extract().length == 0) {
                            mtd.invoke(Model.this, e);
                        } else {
                            Object[] data = new Object[dano.extract().length];
                            for (int i = 0; i < data.length; i++) {
                                data[i] = e.getField(dano.extract()[i]);
                            }
                            mtd.invoke(Model.this, data);
                        }
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    /**
     * filter dragged material
     * if a parameter is configured to be able to drag from then maybe you dont want the
     * actual content to be dragged
     * if you want that when dragging some content from the parameter other content will get dragged
     * then this is the function to use 
     * @param param
     * @param dragged
     * @return
     */
    public Object dragFilter(String param, Object dragged) {
        return dragged;
    }

    /**
     * read dragFilter
     * @param param
     * @param dropped
     * @return
     */
    public Object dropFilter(String param, Object dropped) {
        return dropped;
    }

    private bc.swing.pfrm.ano.PageDef getPageAnnotation() {
        return this.getClass().getAnnotation(bc.swing.pfrm.ano.PageDef.class);
    }

    public void syncToView(String param, DeltaHint hint) {
        if (getPage() == null) {
            return;
        }

        Page.get(this).syncParameterFromModel(param, hint);
    }

    public void syncToView(String param) {
        if (getPage() == null) {
            return;
        }

        Page.get(this).syncParameterFromModel(param);
    }

    public void syncToView() {
        if (getPage() == null) {
            return;
        }

        Page.get(this).syncParametersFromModel();
    }

    public void syncFromView() {
        if (getPage() == null) {
            return;
        }
        Page.get(this).syncParametersFromView();
    }

    public void syncFromView(String param) {
        if (getPage() == null) {
            return;
        }

        Page.get(this).syncParameterFromView(param);
    }

    public String getPageName() {
        return getPageAnnotation().name();
    }

    public ImageIcon getPageIcon() {
        return Page.pageIcon(getPageAnnotation().icon());
    }

    public Class<? extends PageLayout> getPageDefaultView() {
        Class<? extends PageLayout> v = getPageAnnotation().layout();
        return v;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void whenPageCreated(Page page) {
    }

    /**
     * override this so layouts that want to show description about params can know what to show
     * @param param
     * @param value
     * @return 
     */
    public String provideParamValueDescription(String param, Object value) {
        return "";
    }

    public ImageIcon provideParamValueIcon(String param, Object value) {
        return null;
    }

    public static interface ParameterChangeListener {

        void onChange(Model model, String propertyName, Object hint);
    }

    public void configurePageLayout(JPanel layout) {
    }
}
