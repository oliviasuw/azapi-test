/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.ent.Experiment;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inka
 */
@ViewScoped
@ManagedBean
public class ExperimentDetails {

    private Experiment exp;

    public Experiment getExp() {
        if (exp == null) {
            exp = (Experiment) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("selected");
        }
        return exp;
    }

    public void setExp(Experiment exp) {
        this.exp = exp;
    }
}
