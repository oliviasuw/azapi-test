/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.modules.statistics.CPExecutionDataStatisticCollector.CPTestRecord;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.statistics.AbstractStatisticCollector;
import bgu.dcr.az.execs.statistics.info.ExecutionInitializationInfo;
import bgu.dcr.az.orm.api.DBRecord;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author user
 */
public class CPExecutionDataStatisticCollector extends AbstractStatisticCollector<CPData, CPTestRecord> {

    public final String EXECUTION_DATA_INFO_TABLE = "EXECUTION_DATA_INFO";
    
    private long lastRecordIndex = 0;

    public long getLastRecordIndex() {
        return lastRecordIndex;
    }
    
    @Override
    public String getName() {
        return "CP Test data";
    }

    @Override
    public void write(CPTestRecord record) {
        record.index = lastRecordIndex++;
        record.algorithm_instance = getExecution().data().getAlgorithm().getInstanceName();
        record.rvar = getExecution().data().getRunningVar();
        record.test = getExecution().getContainingExperiment().getName();
        insertRecord(record);
    }

    @Override
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable(EXECUTION_DATA_INFO_TABLE, CPTestRecord.class);

        ex.informationStream().listen(ExecutionInitializationInfo.class, m -> {
            write(new CPTestRecord());
        });
    }

    @Override
    protected void plot(QueryDatabase database, Experiment test) {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class CPTestRecord implements DBRecord {

        public long index;
        public String algorithm_instance;
        public double rvar;
        public String test;
    }
}
