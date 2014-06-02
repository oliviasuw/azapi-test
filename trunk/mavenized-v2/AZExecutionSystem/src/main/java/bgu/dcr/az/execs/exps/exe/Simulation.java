/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.common.exceptions.UncheckedInterruptedException;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.execs.lowlevel.TerminationReason;
import bgu.dcr.az.execs.lowlevel.ThreadSafeProcTable;
import bgu.dcr.az.execs.statistics.InfoStreamWrapperProc;
import bgu.dcr.az.execs.statistics.info.ExecutionInitializationInfo;
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.sim.SimulatedMachine;
import bgu.dcr.az.execs.sim.net.BaseMessageRouter;
import bgu.dcr.az.execs.sim.net.MessageRouter;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 *
 * @author bennyl
 * @param <T> type of simulation data
 * @param <R> type of solution data
 */
public class Simulation<T extends SimulationData, R> extends ExecutionTree {

    public static final String EXECUTION_INFO_DATA_TABLE = "EXECUTION_INFO_DATA";

    private final T simulationData;
    private final SimulationResult<R> result = new SimulationResult().toNotRunYetState();
    private ExecutionEnvironment env = ExecutionEnvironment.async;
    private final int executionNumber;
    private final SimulationConfiguration conf;
    private InfoStreamWrapperProc istream;

    public Simulation(int executionNumber, T executionData, SimulationConfiguration conf, Test container) {
        this.simulationData = executionData;
        this.executionNumber = executionNumber;
        this.conf = conf;
        this.installInto(container); //sets the container as my parent..
    }

    @Override
    public final void installInto(ModuleContainer mc) {
        super.installInto(mc);
        install(istream = new InfoStreamWrapperProc(parent().require(InfoStream.class), -1000));
        install(MessageRouter.class, new BaseMessageRouter());
    }

    public ExecutionEnvironment getExecutionEnvironment() {
        return env;
    }

    public void setExecutionEnvironment(ExecutionEnvironment env) {
        this.env = env;
    }

    public T data() {
        return simulationData;
    }

    @Override
    public String getName() {
        return "Simulation " + getSimulationNumber();
    }

    @Override
    public int numChildren() {
        return 0;
    }

    @Override
    public ExecutionTree child(int index) {
        throw new UnsupportedOperationException("no children in a leaf execution");
    }

    @Override
    public Test parent() {
        return (Test) super.parent();
    }

    @Override
    public int countExecutions() {
        return 1;
    }

    @Override
    public Iterator<ExecutionTree> iterator() {
        return Collections.EMPTY_SET.iterator();
    }

    public InfoStream infoStream() {
        return require(InfoStream.class);
    }

    public SimulationResult<R> result() {
        return result;
    }

    @Override
    public void execute() {
        try {
            writeBaseStatisticsFields();
            AdaptiveScheduler sched = require(AdaptiveScheduler.class);

            ThreadSafeProcTable table = new ThreadSafeProcTable();
            table.add(istream);

            createMachines().forEach(table::add);
            istream.writeNow(new ExecutionInitializationInfo());
            TerminationReason terminationResult = sched.schedule(table);

            if (terminationResult.isError()) {
                result.toCrushState(terminationResult.getErrorDescription());
            } else {
                result.toSucceefulState((R) data().currentSolution());
            }

            istream.writeNow(new SimulationTerminationInfo(result, this));
        } catch (InterruptedException ex) {
            throw new UncheckedInterruptedException(ex);
        }
    }

    public SimulationConfiguration configuration() {
        return conf;
    }

    public int getSimulationNumber() {
        return executionNumber;
    }

    private void writeBaseStatisticsFields() {
        EmbeddedDatabaseManager db = require(EmbeddedDatabaseManager.class);
        BaseStatisticFields info = conf.baseStatisticFields();
        info.index = getSimulationNumber();
        db.insert(info);
    }

    private Iterable<SimulatedMachine> createMachines() {
        return () -> { //some lazy ass iterable :)
            return IntStream.range(0, configuration().numMachines()).mapToObj(i -> {
                SimulatedMachine sim = new SimulatedMachine(i, this);
                return sim;
                
            }).iterator();
        };
    }

    public MessageRouter getMessageRouter() {
        return get(MessageRouter.class);
    }

}
