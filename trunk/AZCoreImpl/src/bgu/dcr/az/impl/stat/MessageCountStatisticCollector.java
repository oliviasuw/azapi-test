/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Hooks.BeforeCallingFinishHook;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.stat.DBRecord;
import bgu.dcr.az.api.infra.stat.Database;
import bgu.dcr.az.api.infra.stat.VisualModel;
import bgu.dcr.az.api.infra.stat.vmod.BarVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "msgc-sc")
public class MessageCountStatisticCollector extends AbstractStatisticCollector<MessageCountStatisticCollector.Record> {

    long[] counts;
    @Variable(name = "type", description = "type of the graph to show (BY_AGENT/BY_RUNVAR)", defaultValue = "BY_RUNVAR")
    Type graphType = Type.BY_RUNVAR;

    @Override
    public VisualModel analyze(Database db, Test r) {
        try {
            ResultSet res;
            BarVisualModel bv;
            switch (graphType) {
                case BY_AGENT:
                    bv = new BarVisualModel("Message Count", "Agent", "Avg(Message Sent)");
                    res = db.query(""
                            + "select ALGORITHM_INSTANCE, avg(messages) as m, agent "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by ALGORITHM_INSTANCE, agent "
                            + "order by agent");

                    bv.load("ALGORITHM_INSTANCE", "agent", "m", res);
                    return bv;

                case BY_RUNVAR:
                    String runVar = r.getRunningVarName();
                    bv = new BarVisualModel("Message Count", runVar, "Avg(Message Sent)");
                    res = db.query(""
                            + "select ALGORITHM_INSTANCE, avg(messages) as m, runvar "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by ALGORITHM_INSTANCE, runvar "
                            + "order by runvar");

                    bv.load("ALGORITHM_INSTANCE", "runvar", "m", res);
                    return bv;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageCountStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void hookIn(Agent[] a, final Execution ex) {
        counts = new long[a.length];

        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(Agent a, Message msg) {
                counts[a.getId()]++;
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {

            @Override
            public void hook() {
                for (int i = 0; i < counts.length; i++) {
                    submit(new Record(i, counts[i], test.getCurrentVarValue()));
                }
            }
        }.hookInto(ex);
    }

    @Override
    public String getName() {
        return "Message Count";
    }

    public static class Record extends DBRecord {

        double runVar;
        int agent;
        float messages;

        public Record(int agent, long messages, double runVar) {
            this.agent = agent;
            this.messages = messages;
            this.runVar = runVar;
        }

        @Override
        public String provideTableName() {
            return "Message_Count";
        }
    }

    public static enum Type {

        BY_AGENT,
        BY_RUNVAR
    }
}
