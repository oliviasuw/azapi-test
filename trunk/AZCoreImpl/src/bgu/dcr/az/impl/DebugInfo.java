/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.ano.Configuration;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
@Register(name = "debug-info", display="Debugging Information", visible=false)
public class DebugInfo {
    @Variable(name="test-name", description="failing test name", defaultValue="unnamed")
    String testName = "";
    @Variable(name="algorithm-name", description="failing algorithm name", defaultValue="unnamed")
    String algName = "";
    @Variable(name="name", description="identifier for this debug info", defaultValue="unnamed")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="number", description="number of the failing problem", defaultValue="-1")
    int number = -1;
    
    List<VarAssign> pgenVars = new LinkedList<VarAssign>();

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
    
    public List<VarAssign> getProblemGeneratorConfiguration(){
        return pgenVars;
    }

    @Configuration(name="Problem Generator Variables", description="the problem generator variables that was captured during the failore")
    public void addProblemGeneratorVariable(VarAssign var){
        pgenVars.add(var);
    }

    public List<VarAssign> getProblemGeneratorVariables() {
        return pgenVars;
    }
    
    public String getName() {
        return name;
    }
}
