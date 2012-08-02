/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.lab.util.FacesUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Inna
 */
@ManagedBean(eager=true, name="login")
@SessionScoped
public class Login {

    private String email;
    private String password;
    
    @ManagedProperty("#{dbManager}")
    private DBManager db;
    private User user;    

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

    public User getUser() {
        return user;
    }

    public DBManager getDb() {
        return db;
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String tryLogin(){
        user = User.getByEmail(this.email, db);
        if (user == null){
            FacesUtil.showError("no such user!");
            return null;
        }
        
        return "/lab-pages/profile?faces-redirect=true";
    }
}