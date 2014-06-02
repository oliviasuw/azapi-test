/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.ExperimentProgressInspector;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.exps.ExperimentFailedException;
import java.util.Iterator;

/**
 *
 * @author bennyl
 */
@Register("experiment")
public class DefaultExperimentRoot extends ExecutionTree {

    private String name = "UNNAMED EXPERIMENT";

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @propertyName name
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int numChildren() {
        return amountInstalled(Test.class);
    }

    @Override
    public Test child(int index) {
        return require(Test.class, index);
    }

    @Override
    public void execute() throws ExperimentFailedException {
        final Iterable<Test> tests = requireAll(Test.class);
        for (Test t : tests) {
            infoStream().write(t, Test.class);
            t.execute();
        }
    }

    @Override
    public Iterator<ExecutionTree> iterator() {
        return (Iterator) requireAll(Test.class).iterator();
    }

    @Override
    public int countExecutions() {
        int sum = 0;
        for (ExecutionTree t : this) {
            sum += t.countExecutions();
        }

        return sum;
    }

    @Override
    public void installInto(ModuleContainer mc) {
        super.installInto(mc);
        install(ExperimentProgressInspector.class, new DefaultExperimentProgress());
    }

}
