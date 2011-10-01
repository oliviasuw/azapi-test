/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

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
        return getPageAnnotation().view();
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

    /*public String getPageName() {
        if (pageName == null) {
            return getClass().getAnnotation(bc.swing.pfrm.ano.Page.class).name();
        }else {
            return pageName;
        }
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }*/

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
