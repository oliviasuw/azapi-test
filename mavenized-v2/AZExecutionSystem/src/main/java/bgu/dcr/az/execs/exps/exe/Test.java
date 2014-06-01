/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.exps.ExecutionTree;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 *
 * @author bennyl
 */
@Register(".base-test")
public abstract class Test extends ExecutionTree {

    private String name = "UNNAMED TEST";

    /**
     * @propertyName name
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int countExecutions() {
        return numChildren();
    }

    @Override
    public void execute() {
        final InfoStream infoStream = infoStream();

        for (ExecutionTree e : this) {
            e.installInto(this);
            infoStream.write(e, Simulation.class);
            e.execute();
        }
    }

    @Override
    public final void installInto(ModuleContainer mc) {
        super.installInto(mc);
        initialize((DefaultExperimentRoot) mc);
    }

    public abstract void initialize(DefaultExperimentRoot root);

    @Override
    public abstract Simulation child(int index);

    @Override
    public Iterator<ExecutionTree> iterator() {
        return (Iterator) IntStream.range(0, numChildren()).mapToObj(i -> child(i)).iterator();
    }

}
