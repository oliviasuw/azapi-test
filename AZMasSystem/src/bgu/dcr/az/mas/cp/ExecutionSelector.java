/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.conf.Variable;
import bgu.dcr.az.anop.reg.Register;

/**
 * this class is used to specify a single execution selection when an experiment
 * contains this element it will execute only the specific selected execution
 * and then finish
 *
 * @author bennyl
 */
@Register("execution-selector")
public class ExecutionSelector {

    @Variable(name = "test-name", description = "selected test", defaultValue = "unnamed")
    String testName = "";
    @Variable(name = "algorithm-name", description = "selected algorithm", defaultValue = "unnamed")
    String algName = "";
    //TODO - why is it needed?
    @Variable(name = "name", description = "identifier for this selector info", defaultValue = "unnamed")
    String name = "" + System.currentTimeMillis();
    @Variable(name = "exec-number", description = "the number of the failing execution", defaultValue = "-1")
    int enumber = -1;

    public ExecutionSelector(String testName, String algName, int number) {
        this.testName = testName;
        this.algName = algName;
        this.enumber = number;
    }

    public ExecutionSelector() {
    }

    public String getSelectedTest() {
        return testName;
    }

    public String getSelectedAlgorithmInstanceName() {
        return algName;
    }

    public String getName() {
        return name;
    }

    public int getExecutionNumber() {
        return enumber;
    }

}
