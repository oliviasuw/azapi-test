/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Cpu;
import bgu.dcr.az.lab.data.Experiments;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@SessionScoped
public class CpuEntry {
    int index;
    Cpu cpu;
    Experiments exp;

    public CpuEntry(int index, Cpu cpu) {
        this.index = index;
        this.cpu = cpu;
        this.exp = null;
    }

    public Cpu getCpu() {
        System.out.println("cpu is " + cpu.getData());
        return cpu;
    }

    public Experiments getExp() {
        return exp;
    }

    public int getIndex() {
        System.out.println("index is " + index);
        return index;
    }    
    
    public void setExp(Experiments exp) {
        this.exp = exp;
    }
    
    
       public boolean hasAnExperiment() {
        if (exp != null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[index:" + index + ", cpu:" + cpu.getData() + ", exp:" + exp.getId() + "]";
    }

    
}
