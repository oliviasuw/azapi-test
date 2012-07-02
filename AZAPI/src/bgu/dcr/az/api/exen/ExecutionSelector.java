/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

/**
 * this class is used to specify a single execution selection 
 * when an experiment contains this element it will execute only the specific selected execution
 * and then finish
 * @author bennyl
 */
@Register(name = "execution-selector")
public class ExecutionSelector {
    @Variable(name="test-name", description="selected test", defaultValue="unnamed")
    String testName = "";
    @Variable(name="algorithm-name", description="selected algorithm", defaultValue="unnamed")
    String algName = "";
    //TODO - why is it needed?
    @Variable(name="name", description="identifier for this selector info", defaultValue="unnamed")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="exec-number", description="number of the failing problem", defaultValue="-1")
    int number = -1;
    
    public ExecutionSelector(String testName, String algName, int number) {
        this.testName = testName;
        this.algName = algName;
        this.number = number;
    }

    public ExecutionSelector() {
    }
    
    public String getSelectedTest(){
        return testName;
    }
    public String getSelectedAlgorithmInstanceName(){
        return algName;
    }
    
    public int getSelectedProblemNumber(){
        return number;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "" + testName + "," + algName + "," + number;
    }
    
}
