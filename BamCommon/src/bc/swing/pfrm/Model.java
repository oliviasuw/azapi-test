/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.swing.pfrm.ano.PageDef.DefaultPageView;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private bc.swing.pfrm.ano.PageDef getPageAnnotation(){
        return this.getClass().getAnnotation(bc.swing.pfrm.ano.PageDef.class);
    }
    
    public String getPageName(){
        return getPageAnnotation().name();
    }
    
    public ImageIcon getPageIcon(){
        return Page.pageIcon(getPageAnnotation().icon());
    }
    
    public Class<? extends PageView> getPageDefaultView(){
        Class<? extends PageView> v = getPageAnnotation().view();
        if (v.equals(DefaultPageView.class)){
            String vname = getClass().getCanonicalName();
            if (vname.endsWith("Model")){
                vname = vname.substring(0, vname.length()-"Model".length());
            }
            vname += "View";
            try {
                return (Class<? extends PageView>) Class.forName(vname);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "when trying to generate default view for " + getClass().getSimpleName(), ex);
                return null;
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
}
