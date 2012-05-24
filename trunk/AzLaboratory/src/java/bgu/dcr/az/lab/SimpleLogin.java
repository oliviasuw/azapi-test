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
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class SimpleLogin {

    String email="";
    String password="";

    public SimpleLogin() {
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
        if (email.equals("admin") && password.equals("1234")) {
            System.out.println("admin");
            return "Welcome";
        }
        return "Laboratory";
    }
}