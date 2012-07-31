/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Experiment;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inka
 */
@ViewScoped
@ManagedBean(eager = true, name = "experimentsView")
public class ExperimentsView {

    @ManagedProperty("#{dbManager}")
    private DBManager db;
    @ManagedProperty("#{login}")
    private Login login;
    private boolean userExperiment;
    private boolean isSelectedExperiment;
    private List<Experiment> experiments;
    private Experiment selectedExperiment;

    public DBManager getDb() {
        return db;
    }

    public List<Experiment> getExperiments() {
        if (experiments == null) {
            experiments = Experiment.getAllPublicExperiments(db);
        }
        return experiments;
    }

    public Experiment getSelectedExperiment() {
        return selectedExperiment;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public void setSelectedExperiment(Experiment selectedExperiment) {
        this.selectedExperiment = selectedExperiment;
    }

    public String view() {
        System.out.println("selected experiment is: " + selectedExperiment.getName() + ", " + selectedExperiment.getDescription());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selected", selectedExperiment);
        return "experiment-details?faces-redirect=true";
    }

    public String edit() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selected", selectedExperiment);
        return "experiment-edit?faces-redirect=true";
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public boolean isUserExperiment() {
        if (selectedExperiment != null) {
            userExperiment = login.getUser().equals(selectedExperiment.getOwner());
        } else {
            userExperiment = true;
        }
        return userExperiment;
    }

    public boolean isIsSelectedExperiment() {
           if (selectedExperiment != null) {
            isSelectedExperiment = false;
        } else {
            isSelectedExperiment = true;
        }
        return isSelectedExperiment;
    }

    public void setIsSelectedExperiment(boolean isSelectedExperiment) {
        this.isSelectedExperiment = isSelectedExperiment;
    }

    
    
    public void setUserExperiment(boolean userExperiment) {
        this.userExperiment = userExperiment;
    }

    private void fillExperiments() {
//        experiments = new LinkedList<Experiment>();
//        List<Experiment> content = new LinkedList<Experiment>();
    }
}
