/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Experiment;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inka
 */
@ViewScoped
@ManagedBean
public class ExperimentEdit {

    @ManagedProperty("#{dbManager}")
    private DBManager db;
    private Experiment exp;

    public Experiment getExp() {
        if (exp == null) {
            exp = (Experiment) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("selected");
        }
        return exp;
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public DBManager getDb() {
        return db;
    }

    public void setExp(Experiment exp) {
        this.exp = exp;
    }

    public String save() {
        db.update(exp);
        return "view-experiments?faces-redirect=true";
    }
}
