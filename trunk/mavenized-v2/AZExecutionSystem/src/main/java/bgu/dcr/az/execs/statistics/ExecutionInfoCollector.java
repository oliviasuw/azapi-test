/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.execs.statistics.ExecutionInfoCollector.ExecutionInfo;
import bgu.dcr.az.execs.statistics.info.ExecutionInitializationInfo;
import bgu.dcr.az.orm.api.DBRecord;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;

/**
 *
 * @author user
 * @param <T>
 * @param <R>
 */
public abstract class ExecutionInfoCollector<T, R extends ExecutionInfo> implements ExecutionService<T> {

    public static final String EXECUTION_INFO_DATA_TABLE = "EXECUTION_INFO_DATA";

    private long lastRecordIndex = 0;

    public long getLastRecordIndex() {
        return lastRecordIndex;
    }

    @Override
    public void initialize(Experiment ex) {
    }

    @Override
    public void initialize(Execution<T> ex) throws InitializationException {
        final EmbeddedDatabaseManager db = (EmbeddedDatabaseManager) ex.require(EmbeddedDatabaseManager.class);
        db.defineTable(EXECUTION_INFO_DATA_TABLE, getDataRecordClass());

        ex.informationStream().listen(ExecutionInitializationInfo.class, m -> {
            R record = getDataRecord(ex);
            record.index = lastRecordIndex++;
            db.insert(record);
        });
    }

    protected abstract R getDataRecord(Execution<T> ex);

    protected abstract Class<R> getDataRecordClass();

    public static class ExecutionInfo implements DBRecord {

        public long index;
    }
}
