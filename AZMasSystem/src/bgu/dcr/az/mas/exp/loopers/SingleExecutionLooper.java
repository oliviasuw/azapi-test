/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp.loopers;

import bgu.dcr.az.anop.Register;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.mas.cp.CPExperiment;
import bgu.dcr.az.mas.exp.Execution;
import bgu.dcr.az.mas.exp.Looper;
import bgu.dcr.az.mas.exp.executions.AbstractExecution;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executors;

/**
 *
 * @author User
 */
public class SingleExecutionLooper implements Looper {

    @Override
    public int count() {
        return 1;
    }

    @Override
    public void configure(int i, Collection<Configuration> configurations) {
    }

}
