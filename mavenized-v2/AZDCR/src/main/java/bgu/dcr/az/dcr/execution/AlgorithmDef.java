/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.conf.registery.Register;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
@Register("algorithm")
public class AlgorithmDef {

    private String name;
    private List<AlgorithemVariableAssignment> assignments = new LinkedList<>();

    /**
     * @return the algorithm name
     * @propertyName name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AlgorithmDef{" + "name=" + name + '}';
    }

    public String getInstanceName() {
        return getName();
    }

    /**
     * @propertyName assignments
     * @return 
     */
    public List<AlgorithemVariableAssignment> getAssignments() {
        return assignments;
    }

}
