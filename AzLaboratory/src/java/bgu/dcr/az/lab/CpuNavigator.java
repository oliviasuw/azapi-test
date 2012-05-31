/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.io.File;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author Administrator
 */
@ManagedBean(eager=true)
@ApplicationScoped
public class CpuNavigator {
    private String[][] pages = {{"Managment","Managment"}, {"Executions","Executions"}, {"Exit CPU","Welcome"}};
    
    
    
    public String[][] getPages(){
        return pages;
    }
    
    public String gotoOther(){
        return "main/other";
    }
    
    public String[] getCurrentPageName(){
        FacesContext context = FacesContext.getCurrentInstance();
        File currentUrl = new File(context.getViewRoot().getViewId());
        final String name = currentUrl.getName().replaceAll("\\.xhtml", "");
        //System.out.println("name is " + name);
        return (name.equals("index")? getPage("Managment"): getPage(name));
    }
    
    
    private String[] getPage(String name){
        for (int i=0; i<pages.length;i++){
            if (pages[i][0].equals(name)) return pages[i];
        }
        return null;
    }
}
