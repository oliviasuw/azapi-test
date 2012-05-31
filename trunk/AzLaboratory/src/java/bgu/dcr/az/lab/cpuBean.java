/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Cpu;
import bgu.dcr.az.lab.data.Experiments;
import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import org.primefaces.event.DragDropEvent;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
public class CpuBean{
    
    Experiments selectedExperiment;
    HashMap<Integer, List<Experiments>> droppedExperiments = new HashMap<Integer, List<Experiments>>();

    public List<Experiments> getExperiments() throws SQLException {
        List<Experiments> ans = DBManager.UNIT.getUserExperiments((Users) SessionManager.getFromSessionMap(SessionManager.CURRENT_USER));
        System.out.println("GOT Experiments " + ans);
        return ans;
    }
    
    public List<Cpu> getCpus() throws SQLException {
        List<Cpu> ans = DBManager.UNIT.getUserCpus((Users) SessionManager.getFromSessionMap(SessionManager.CURRENT_USER));
        System.out.println("GOT CPUS " + ans);
        return ans;
    }
    
    

    public void deleteExperiment() {
        System.out.println("DELETED ");
    }

    public void deleteAlgorithm() {
    }

    public Experiments getSelectedExperiment() {
        System.out.println("GOT selected experiment " + selectedExperiment.getId());
        return selectedExperiment;
    }

    public void setSelectedExperiment(Experiments selectedExperiment) {
        System.out.println("SET selected experiment " + selectedExperiment.getId());
        this.selectedExperiment = selectedExperiment;
    }
    
    public List<Experiments> getDroppedExperiments(int id){
        System.out.println("GOT dropped experiments " + droppedExperiments.get(id));
        return droppedExperiments.get(id);
    }
    
    public void onExperimentDrop(DragDropEvent ddEvent) {  
        Experiments exp = ((Experiments) ddEvent.getData());  
        System.out.println("SET dropped experiments " + droppedExperiments.get(1) + "to " + exp);
        (droppedExperiments.get(1)).add(exp);
    } 
}
