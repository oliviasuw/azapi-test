/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.conf.registery.Register;

/**
 *
 * @author User
 */
@Register("failure-description")
public class FailureDescription {

    private String description;
    private String failingTestName;
    private String failingAlgorithmName;
    private int failingExecutionNumber;

    public FailureDescription() {
    }

    public FailureDescription(String description, String failingTestName, String failingAlgorithmName, int failingExecutionNumber) {
        this.description = description;
        this.failingTestName = failingTestName;
        this.failingAlgorithmName = failingAlgorithmName;
        this.failingExecutionNumber = failingExecutionNumber;
    }

    /**
     * @propertyName description
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @propertyName algorithm
     * @return
     */
    public String getFailingAlgorithmName() {
        return failingAlgorithmName;
    }

    public void setFailingAlgorithmName(String failingAlgorithmName) {
        this.failingAlgorithmName = failingAlgorithmName;
    }

    /**
     * @propertyName execution-number
     * @return
     */
    public int getFailingExecutionNumber() {
        return failingExecutionNumber;
    }

    public void setFailingExecutionNumber(int failingExecutionNumber) {
        this.failingExecutionNumber = failingExecutionNumber;
    }

    /**
     * @propertyName test-name
     * @return
     */
    public String getFailingTestName() {
        return failingTestName;
    }

    public void setFailingTestName(String failingTestName) {
        this.failingTestName = failingTestName;
    }

}
