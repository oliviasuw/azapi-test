/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.dcr.api.experiment.CPTest;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.statistics.AbstractStatisticCollector;
import bgu.dcr.az.execs.orm.api.QueryDatabase;

/**
 *
 * @author user
 */
public abstract class AbstractCPStatisticCollector extends AbstractStatisticCollector {

    @Override
    protected void plot(QueryDatabase database, Test test) {
        plot(database, (CPTest) test);
    }

    /**
     *
     * @param ex
     * @param database
     */
    @Override
    protected final void initialize(final Simulation ex, DefinitionDatabase database) {
        initialize(database, ex);
    }

    protected abstract void initialize(DefinitionDatabase database, Simulation<CPData, CPSolution> sim);

    protected abstract void plot(QueryDatabase database, CPTest test);
}
