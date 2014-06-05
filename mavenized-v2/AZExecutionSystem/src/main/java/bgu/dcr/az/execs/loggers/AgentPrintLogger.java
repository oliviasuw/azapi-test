/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.loggers.LogManager.LogRecord;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.RecordAccessor;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class AgentPrintLogger extends Logger {

    public void log(int aid, String msg) {
        commitLog(new AgentPrintLogRecord(aid, msg));
    }

    @Override
    public String toString() {
        return "Agent print logger";
    }

    @Override
    public void initialize(DefinitionDatabase database) {
        database.defineTable("AGENT_PRINT_LOG", AgentPrintLogRecord.class);
    }

    @Override
    public List<LogRecord> getRecords(String test, int simulation) throws SQLException {
        Data rawData = query("SELECT * FROM AGENT_PRINT_LOG WHERE test = '" + test + "' AND simulation_index = " + simulation);
        LinkedList<LogRecord> result = new LinkedList<>();

        rawData.forEach(r -> {
            AgentPrintLogRecord rr = new AgentPrintLogRecord(r.getInt("aid"), r.getString("msg"));
            rr.test = test;
            rr.simulation_index = simulation;
            rr.sharedIndex = r.getLong("sharedIndex");
            rr.time = r.getLong("time");
            result.add(rr);
        });
        return result;
    }

    public static class AgentPrintLogRecord extends LogRecord {

        public String msg;

        public AgentPrintLogRecord(int aid, String msg) {
            this.aid = aid;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "Log from Agent " + aid + ": " + msg;
        }
    }
}
