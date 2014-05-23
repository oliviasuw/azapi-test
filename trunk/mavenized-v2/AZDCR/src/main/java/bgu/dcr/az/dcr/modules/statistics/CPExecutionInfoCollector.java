/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.modules.statistics.CPExecutionInfoCollector.CPExecutionInfoRecord;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.statistics.ExecutionInfoCollector;

/**
 *
 * @author user
 */
public class CPExecutionInfoCollector extends ExecutionInfoCollector<CPData, CPExecutionInfoRecord> {

    @Override
    public CPExecutionInfoRecord getDataRecord(Execution<CPData> ex) {
        CPExecutionInfoRecord record = new CPExecutionInfoRecord();
        record.algorithm_instance = ex.data().getAlgorithm().getInstanceName();
        record.rvar = ex.data().getRunningVar();
        record.test = ex.getContainingExperiment().getName();
        return record;
    }

    @Override
    protected Class<CPExecutionInfoRecord> getDataRecordClass() {
        return CPExecutionInfoRecord.class;
    }

    public static class CPExecutionInfoRecord extends ExecutionInfoCollector.ExecutionInfo {

        public String algorithm_instance;
        public double rvar;
        public String test;
    }
}
