/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

/**
 *
 * @author bennyl
 */
@Register(name = "debug-info")
public class DebugInfo {
    @Variable(name="test-name", description="failing test name", defaultValue="unnamed")
    String testName = "";
    @Variable(name="algorithm-name", description="failing algorithm name", defaultValue="unnamed")
    String algName = "";
    @Variable(name="name", description="identifier for this debug info", defaultValue="unnamed")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="number", description="number of the failing problem", defaultValue="-1")
    int number = -1;
    
    public DebugInfo(String testName, String algName, int number) {
        this.testName = testName;
        this.algName = algName;
        this.number = number;
    }

    public DebugInfo() {
    }
    
    public String getTestName(){
        return testName;
    }
    public String getAlgorithmName(){
        return algName;
    }
    
    public int getFailedProblemNumber(){
        return number;
    }
    
    public String getName() {
        return name;
    }
}
