/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp.executions;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.ThreadSafeProcTable;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.exp.Execution;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class BasicExecution implements Execution {

    private Problem p;
    private List<Agent> agentsToRun;
    private Scheduler scheduler;

    public BasicExecution(Problem p, List<Agent> agentsToRun, Scheduler scheduler) {
        this.p = p;
        this.agentsToRun = agentsToRun;
        this.scheduler = scheduler;
    }

    @Override
    public void execute() throws ExperimentExecutionException, InterruptedException {
        ThreadSafeProcTable table = new ThreadSafeProcTable();

        for (Agent a : agentsToRun) {
            table.add(new SingleAgentRunner(a));
        }

        scheduler.schedule(table);
    }

}
