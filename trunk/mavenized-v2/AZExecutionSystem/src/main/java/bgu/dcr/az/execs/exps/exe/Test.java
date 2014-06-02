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
import bgu.dcr.az.execs.exps.ExperimentProgressInspector;
import static bgu.dcr.az.execs.exps.exe.Simulation.EXECUTION_INFO_DATA_TABLE;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author bennyl
 */
@Register(".base-test")
public abstract class Test extends ExecutionTree {

    private String name = "UNNAMED TEST";
    private EmbeddedDatabaseManager dbm;

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
            Simulation sim = (Simulation) e;
            sim.installInto(this);
            
            //install statistic support
            BaseStatisticFields info = sim.configuration().baseStatisticFields();
            dbm.defineTable(EXECUTION_INFO_DATA_TABLE, info.getClass());
            
            //notify the new simulation
            infoStream.write(e, Simulation.class);
            
            e.execute();
        }
    }

    @Override
    public final void installInto(ModuleContainer mc) {
        super.installInto(mc);
        dbm = require(EmbeddedDatabaseManager.class);
        initialize((DefaultExperimentRoot) mc);

        if (mc.isInstalled(DefaultExperimentProgress.class)) {
            Class<? extends ExperimentProgressInspector>[] c = supplyProgressInspectors();
            for (Class<? extends ExperimentProgressInspector> inspector : c) {
                if (!isInstalled(inspector)) {
                    try {
                        parent().parent().install(inspector.newInstance());
                    } catch (InstantiationException | IllegalAccessException ex) {
                        System.err.println("cannot install progress inspector: " + inspector.getSimpleName());
                    }
                }
            }
        }

    }

    public abstract void initialize(DefaultExperimentRoot root);

    @Override
    public abstract Simulation child(int index);

    @Override
    public Iterator<ExecutionTree> iterator() {
        return (Iterator) IntStream.range(0, numChildren()).mapToObj(i -> child(i)).iterator();
    }

    protected Class<? extends ExperimentProgressInspector>[] supplyProgressInspectors() {
        return new Class[0];
    }

}
