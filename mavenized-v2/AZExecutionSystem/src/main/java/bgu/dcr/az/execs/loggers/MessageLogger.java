/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.api.loggers.LogManager.LogRecord;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.statistics.info.MessageInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import java.util.stream.IntStream;

/**
 *
 * @author bennyl
 */
public class MessageLogger implements Logger {

    @Override
    public void initialize(LogManager manager, Execution execution, DefinitionDatabase database) {
        database.defineTable("MESSAGES_LOG", MessageLogRecord.class);

        execution.informationStream().listen(MessageInfo.class, m -> {
            manager.commit(this, new MessageLogRecord(m.getMessageName(), m.getSender(), m.getRecepient(), MessageInfo.OperationType.Sent.equals(m.getType())));
        });
    }

    @Override
    public String getName() {
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
    }
}
