/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author BLutati
 */
public class PageContainer {
    private LinkedHashMap<String, Page> pages;
    private String activePageName;
    private Page activePage;
    private List<Listener> listeners;
    private MenuListModel menuListModel;

    public PageContainer() {
        pages = new LinkedHashMap<String, Page>();
        listeners = new LinkedList<Listener>();
        activePage = null;
        activePageName = null;
        menuListModel = new MenuListModel();
        addListener(menuListModel);
    }

    public MenuListModel getMenuListModel() {
        return menuListModel;
    }

    public void flipNextPage(){
        int idx;
        String[] all = getPages();
        for (idx=0; idx<all.length; idx++){
            if (all[idx].equals(activePage.getName())) {
                setActivePage(all[(idx+1)%all.length]);
                return;
            }
        }
    }

    public void flipBackPage(){
        int idx;
        String[] all = getPages();
        for (idx=0; idx<all.length; idx++){
            if (all[idx].equals(activePage.getName())) {
                setActivePage(all[(idx-1+all.length)%all.length]);
                return;
            }
        }
    }

    public final void addListener(Listener l){
        listeners.add(l);
    }

    public void removeListener(Listener l){
        listeners.remove(l);
    }

    public void addPage(Page page){
        pages.put(page.getName(), page);
        for (Listener l : listeners) l.onPageAdded(this, page.getName());
    }

    public Page removePage(String name){
        Page ret = pages.remove(name);
        if (ret != null){
            for (Listener l : listeners) l.onPageRemoved(this, name, ret);
        }

        return ret;
    }

    public boolean isThrerActivePage(){
        return activePage != null;
    }

    public Page getPage(String name){
        return pages.get(name);
    }

    public String[] getPages(){
        return pages.keySet().toArray(new String[0]);
    }

    public void setActivePage(String name){
        activePage = pages.get(name);
        activePageName = name;
        for (Listener l : listeners){
            l.onActivePageChanged(this, name);
        }
    }
    
    public void setGuestPage(Page p){
        
    }

    public boolean isActivePageLast(){
        if (activePage == null) return false;
        final String[] ordered = getPages();
        return ordered[ordered.length-1].equals(activePage.getName());
    }

    public boolean isActivePageFirst(){
        if (activePage == null) return false;
        final String[] ordered = getPages();
        return ordered[0].equals(activePage.getName());
    }

    public void unSetActivePage(){
        for (Listener l : listeners){
            l.onActivePageChanged(this, null);
        }
    }

    public String getActivePageName(){
        return activePageName;
    }

    public Page getActivePage(){
        return activePage;
    }

    public static interface Listener{
        void onPageAdded(PageContainer source, String name);
        void onPageRemoved(PageContainer source, String name, Page page);
        void onActivePageChanged(PageContainer source, String active);
    }

    public class MenuListModel extends AbstractListModel implements Listener{

        @Override
        public int getSize() {
            return pages.size();
        }

        @Override
        public Object getElementAt(int index) {
            return pages.keySet().toArray()[index];
        }

        @Override
        public void onPageAdded(PageContainer source, String name) {
            fireIntervalAdded(source, getSize()-1, getSize());
        }

        @Override
        public void onPageRemoved(PageContainer source, String name, Page page) {
            fireIntervalRemoved(source, getSize(), getSize());
        }

        @Override
        public void onActivePageChanged(PageContainer source, String active) {
            //nothing
        }

    }
}
