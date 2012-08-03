/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Inka
 */
@ViewScoped
@ManagedBean
public class Profile implements Serializable {

    @ManagedProperty("#{login}")
    private Login login;
    @ManagedProperty("#{dbManager}")
    private DBManager db;
    private Code[] codes;
    private boolean edit = false;

    public Code[] getCodes() {
        fillClasses();
        return codes;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public void setCodes(Code[] codes) {
        this.codes = codes;
    }

    public DBManager getDb() {
        return db;
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
    
    public void edit(){
        edit = true;
    }
    
    public void doneEditing(){
        edit = false;
    }

    private void fillClasses() {
        List<Code> content = login.getUser().getAllUploadedCodes(db);
        int i = 0;
        for (Code c : content) {
            codes[i++] = c;
        }
    }
}
