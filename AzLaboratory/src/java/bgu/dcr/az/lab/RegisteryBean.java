/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author kdima85
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class RegisteryBean {
    
    String name;
    String password;
    String email;



    
    public String handleRegistry(){
        DBManager.UNIT.registerNewUser(new Users(email, password, name, new byte[0], false));
        return "Laboratory";
    }
    
    

    
  

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
}
