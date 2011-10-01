/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.pages;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author bennyl
 */
public class PageContainerModel {
    HashMap<String, Page> pages;
    Page activePage;
    LinkedList<Listener> listeners;

    public PageContainerModel() {
        pages = new HashMap<String, Page>();
        activePage = null;
        listeners = new LinkedList<Listener>();
    }
    
    public Page getActivePage() {
        return activePage;
    }
    
    public boolean setActivePage(String pageName){
        if (pages.containsKey(pageName)){
            activePage = pages.get(pageName);
            for (Listener listener : listeners) {
                listener.onActivePageChanged(this, activePage);
            }
            return true;
        }
        
        return false;
    }
    
    public void addListener(Listener listener){
        this.listeners.add(listener);
    }
    
    public void registerPage(Page page){
        for (Listener listener : listeners) {
                listener.onPageAdded(this, page);
            }
        pages.put(page.getName(), page);
    }
    
    public static interface Listener{
        void onActivePageChanged(PageContainerModel source, Page activePage);
        void onPageAdded(PageContainerModel source, Page added);
    }
}
