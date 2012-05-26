/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author kdima85
 */
public class SessionManager {
    public static final String CURRENT_USER = "current user";
    
    
    private static Map getSessionMap(){
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }
    
    public static Object getFromSessionMap(String Data){
         return getSessionMap().get(Data);
    }
    
    
    public static void putInSessionMap(String key, Object value){
        getSessionMap().put(key, value);
    }
    
    public static Object removeFromSessionMap(String Key){
        return getSessionMap().remove(Key);
    }
}
