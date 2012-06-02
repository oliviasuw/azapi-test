/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Cpu;
import bgu.dcr.az.lab.data.Experiments;
import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.DragDropEvent;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@SessionScoped
public class CpuBean {

    Experiments selectedExperiment;
    List<CpuEntry> cpuMap;
    List<Experiments> experiments;

    public CpuBean() {
        Users user = (Users) SessionManager.getFromSessionMap(SessionManager.CURRENT_USER);
        List<Cpu> cpus = DBManager.UNIT.getUserCpus(user);
        populateCpuMap(cpus);
        System.out.println("User is " + user.getName() + "CPUS are here " + cpus);
//        droppedExperiments = new Experiments[cpus.size()];
        experiments = DBManager.UNIT.getUserExperiments((Users) SessionManager.getFromSessionMap(SessionManager.CURRENT_USER));
    }

    public List<CpuEntry> getCpuMap() {
        return cpuMap;
    }

    public List<Experiments> getExperiments() {
        return experiments;
    }

    public Experiments getSelectedExperiment() {
//        System.out.println("GOT selected experiment " + selectedExperiment.getId());
        return selectedExperiment;
    }

    public void setSelectedExperiment(Experiments selectedExperiment) {
//        System.out.println("SET selected experiment " + selectedExperiment.getId());
        this.selectedExperiment = selectedExperiment;
    }

    public void onExperimentDrop(DragDropEvent ddEvent) {
        Experiments exp = ((Experiments) ddEvent.getData());
        int id = getId(ddEvent.getDropId());
        cpuMap.get(getEntryByIndex(id)).setExp(exp);
        System.out.println("id is" + id);

//        System.out.println("SET dropped experiments " + droppedExperiments.get(1) + "to " + exp);

//        droppedExperiments[id] = exp;
//        System.out.println("SET dropped experiments " );
    }

    private int getId(String dropId) {
        return Integer.valueOf(dropId.split(":x")[1]);
    }

    private void populateCpuMap(List<Cpu> cpus) {
        cpuMap = new LinkedList<CpuEntry>();
        for (int i = 0; i < cpus.size(); i++) {
            cpuMap.add(new CpuEntry(i, cpus.get(i)));
        }
        System.out.println("CPU-MAP is " + cpuMap.toString());
    }

    private int getEntryByIndex(int index) {

        for (int i = 0; i < cpuMap.size(); i++) {
            CpuEntry cpu = cpuMap.get(i);
            if (cpu.getIndex() == index) {
                return i;
            }
        }
        return 0;
    }
}
