/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.statistics.AbstractStatisticCollector;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author user
 */
public abstract class AbstractCPStatisticCollector extends AbstractStatisticCollector<CPData> {

    @Override
    protected void plot(QueryDatabase database, Experiment test) {
        plot(database, (CPExperimentTest) test);
    }

    protected abstract void plot(QueryDatabase database, CPExperimentTest test);
}
