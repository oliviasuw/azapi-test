/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.loggers.LogManager.LogRecord;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.statistics.info.MessageInfo;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class MessageLogger extends Logger {

    @Override
    public void initialize(DefinitionDatabase database) {
        database.defineTable("MESSAGES_LOG", MessageLogRecord.class);

        experiment().infoStream().listen(MessageInfo.class, m -> {
            commitLog(new MessageLogRecord(m.getMessageName(), m.getSender(), m.getRecepient(), MessageInfo.OperationType.Sent.equals(m.getType())));
        });
    }

    @Override
    public List<LogRecord> getRecords(String test, int simulation) throws SQLException {
        Data rawData = query("SELECT * FROM MESSAGES_LOG WHERE test = '" + test + "' AND simulation_index = " + simulation);
        LinkedList<LogRecord> result = new LinkedList<>();

        rawData.forEach(r -> {
            MessageLogRecord rr = new MessageLogRecord(r.getString("name"), r.getInt("sender"), r.getInt("recepient"), r.getBoolean("sent"));
            rr.aid = r.getInt("aid");
            rr.test = test;
            rr.simulation_index = simulation;
            rr.sharedIndex = r.getLong("sharedIndex");
            rr.time = r.getLong("time");
            result.add(rr);
        });
        return result;
    }

    @Override
    public String toString() {
        return "Message logger";
    }

    public static class MessageLogRecord extends LogRecord {

        public String name;
        public int sender;
        public int recepient;
        public boolean sent;

        public MessageLogRecord(String name, int sender, int recepient, boolean sent) {
            this.aid = sent ? sender : recepient;
            this.name = name;
            this.sender = sender;
            this.recepient = recepient;
            this.sent = sent;
        }

        @Override
        public String toString() {
            if (sent) {
                return "Message '" + name + " was sent from " + sender + " to " + recepient;
            }
            return "Message '" + name + " was received at " + recepient + " from " + sender;

        }
    }
}
