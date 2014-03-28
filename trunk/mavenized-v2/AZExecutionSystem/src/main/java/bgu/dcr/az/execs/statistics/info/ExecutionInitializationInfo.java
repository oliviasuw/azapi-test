/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics.info;

/**
 *
 * @author User
 */
public class ExecutionInitializationInfo {

    private int numberOfCores;

    public ExecutionInitializationInfo(int numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }

}
