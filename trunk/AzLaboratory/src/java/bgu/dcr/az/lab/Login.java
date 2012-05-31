/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.*;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@SessionScoped
public class Login {

    String email = "";
    String password = "";
    Login user;

    public Login() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String checkValidUser() {
        System.out.println("user name - trying to get the user" );
        Users u = DBManager.UNIT.isVerifiedUserCredentials(this.email, this.password);
        System.out.println("user name " + u.getName());
        if (u != null) {
            SessionManager.putInSessionMap(SessionManager.CURRENT_USER, u);
            return "Welcome";
        }
        
        return "Laboratory";
    }
}