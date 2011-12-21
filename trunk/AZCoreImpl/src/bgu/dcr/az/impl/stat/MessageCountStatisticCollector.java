/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.stat;

import bgu.dcr.az.api.Agent;
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
@Register(name = "msgc-sc", display="Avarage Number of Messages Sent")
public class MessageCountStatisticCollector extends AbstractStatisticCollector<MessageCountStatisticCollector.Record> {

    long[] counts;
    @Variable(name = "type", description = "type of the graph to show (BY_AGENT/BY_RUNVAR)", defaultValue="BY_RUNVAR")
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
                            + "select algorithm, avg(messages) as m, agent "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by algorithm, agent "
                            + "order by agent");

                    bv.load("algorithm", "agent", "m", res);
                    return bv;

                case BY_RUNVAR:
                    String runVar = r.getRunningVarName();
                    bv = new BarVisualModel("Message Count", runVar, "Avg(Message Sent)");
                    res = db.query(""
                            + "select algorithm, avg(messages) as m, runvar "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by algorithm, runvar "
                            + "order by runvar");

                    bv.load("algorithm", "runvar", "m", res);
                    return bv;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageCountStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void hookIn(Agent[] a, Execution ex) {
        counts = new long[a.length];
        for (Agent agt : a) {
            agt.hookIn(new BeforeMessageSentHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    counts[a.getId()]++;
                }
            });
        }

        a[0].hookIn(new BeforeCallingFinishHook() {

            @Override
            public void hook(Agent a) {
                for (int i = 0; i < counts.length; i++) {
//                    System.out.println("rval: " +  test.getCurrentVarValue());
                    submit(new Record(a.getAlgorithmName(), i, counts[i], test.getCurrentVarValue()));
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Message Count";
    }

    public static class Record extends DBRecord {

        float runVar;
        String algorithm;
        int agent;
        float messages;

        public Record(String algorithm, int agent, long messages, float runVar) {
            this.algorithm = algorithm;
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
