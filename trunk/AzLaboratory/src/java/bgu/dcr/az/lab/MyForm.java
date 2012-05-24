/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author Administrator
 */
@ManagedBean
public class MyForm {
    private String lang = "?";

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String ok(){
        return "main/other";
    }
}
