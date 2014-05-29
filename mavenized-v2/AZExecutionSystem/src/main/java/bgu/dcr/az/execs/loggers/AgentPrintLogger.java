/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.loggers.LogManager.LogRecord;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;

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

    public static class AgentPrintLogRecord extends LogRecord {

        public String msg;

        public AgentPrintLogRecord(int aid, String msg) {
            this.aid = aid;
            this.msg = msg;
        }

    }
}
