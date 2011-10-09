/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.swing.pfrm.ano.PageDef.DefaultPageView;
import bc.swing.pfrm.layouts.CenterLayout;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class Model {

    private LinkedList<ParameterChangeListener> listeners = new LinkedList<ParameterChangeListener>();
    private Page page;

    public void fireParamChanged(String propertyName) {
        for (ParameterChangeListener l : listeners) {
            l.onChange(this, propertyName, null);
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
    public Object dragFilter(String param, Object dragged){
        return dragged;
    }

    /**
     * read dragFilter
     * @param param
     * @param dropped
     * @return
     */
    public Object dropFilter(String param, Object dropped){
        return dropped;
    }

    private bc.swing.pfrm.ano.PageDef getPageAnnotation(){
        return this.getClass().getAnnotation(bc.swing.pfrm.ano.PageDef.class);
    }

    public void syncToView(String param, DeltaHint hint){
        if (getPage() == null) return;

        Page.get(this).syncParameterFromModel(param, hint);
    }

    public void syncToView(String param){
        if (getPage() == null) return;

        Page.get(this).syncParameterFromModel(param);
    }

    public void syncFromView(){
        if (getPage() == null) return;
        Page.get(this).syncParametersFromView();
    }
    
    public void syncFromView(String param){
        if (getPage() == null) return;

        Page.get(this).syncParameterFromView(param);
    }

    public String getPageName(){
        return getPageAnnotation().name();
    }
    
    public ImageIcon getPageIcon(){
        return Page.pageIcon(getPageAnnotation().icon());
    }
    
    public Class<? extends PageView> getPageDefaultView(){
        Class<? extends PageView> v = getPageAnnotation().layout();
        if (v.equals(DefaultPageView.class)){
            String vname = getClass().getCanonicalName();
            if (vname.endsWith("Model")){
                vname = vname.substring(0, vname.length()-"Model".length());
            }
            vname += "View";
            try {
                return (Class<? extends PageView>) Class.forName(vname, false, ClassLoader.getSystemClassLoader());
            } catch (ClassNotFoundException ex) {
                return CenterLayout.class;
            }
        }else {
            return v;
        }
    }
    
    public void addParameterChangedListener(ParameterChangeListener l) {
        listeners.add(l);
    }

    public void removeParameterChangedListener(ParameterChangeListener l) {
        listeners.remove(l);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void whenPageCreated(Page page){
        
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
    
    public void configurePageLayout(JPanel layout){
        
    }
    
}
