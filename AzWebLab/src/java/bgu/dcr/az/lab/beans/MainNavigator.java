/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import java.io.File;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author Administrator
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class MainNavigator {

    private String[][] pages = {{"Home", "home"}, {"Download", "download"}, {"Features", "features"}, {"Laboratory", "login"}, {"Contact", "contact"}};

    public String[][] getPages() {
        return pages;
    }

    public String gotoOther() {
        return "/other";
    }

    public String[] getCurrentPageName() {
        FacesContext context = FacesContext.getCurrentInstance();
        File currentUrl = new File(context.getViewRoot().getViewId());
        final String name = currentUrl.getName().replaceAll("\\.xhtml", "");
        return (name.equals("index") ? getPage("home") : getPage(name));
    }
    
    private String[] getPage(String name) {
        for (int i = 0; i < pages.length; i++) {
            if (pages[i][1].equals(name)) {
                return pages[i];
            }
        }
        return null;
    }
}
