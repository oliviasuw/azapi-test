/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.api.modules.AbstractCPStatisticCollector;
import bgu.dcr.az.conf.api.Variable;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.dcr.api.experiment.CPTest;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.QueryDatabase;

/**
 *
 * @author bennyl
 */
@Register("msgc-sc")
public class MessageCountStatisticCollector extends AbstractCPStatisticCollector {

    @Variable(name = "type", description = "type of the graph to show (BY_AGENT/BY_RUNVAR)", defaultValue = "BY_RUNVAR")
    Type graphType = Type.BY_RUNVAR;

    @Override
    public String getName() {
        return "Message Count";
    }

    @Override
    protected void plot(QueryDatabase database, CPTest test) {
        switch (graphType) {
            case BY_AGENT:
                plotBarChart(database.query(""
                        + "select ALGORITHM_INSTANCE, avg(messages) as m, agent "
                        + "from Message_count "
                        + "where test = ? "
                        + "group by ALGORITHM_INSTANCE, agent "
                        + "order by agent", test.getName()), "AGENT", "M", "ALGORITHM_INSTANCE");
                break;

            case BY_RUNVAR:
                AdditionalBarChartProperties properties = new AdditionalBarChartProperties();
                properties.setTitle(getName());
                properties.setValueFieldLabel("Avg(Message Sent)");
                properties.setCategoryAxisLabel(test.getLooper().getRunningVariableName());

                plotBarChart(database.query(""
                        + "select ALGORITHM_INSTANCE, avg(messages) as m, RVAR "
                        + "from Message_count "
                        + "where test = ? "
                        + "group by ALGORITHM_INSTANCE, RVAR "
                        + "order by RVAR", test.getName()), "RVAR", "M", "ALGORITHM_INSTANCE", properties);

        }

    }

    @Override
    protected void initialize(DefinitionDatabase database, Simulation<CPData, CPSolution> ex) {
        database.defineTable("MESSAGE_COUNT", MessagesRecord.class);

        ex.infoStream().listen(SimulationTerminationInfo.class, t -> {
            final long[] messagec = ex.getMessageRouter().getMessageReceivedCountPerAgent();
            for (int i = 0; i < messagec.length; i++) {
                write(new MessagesRecord(i, messagec[i]));
            }
        });
    }

    public static class MessagesRecord extends StatisticRecord {

        int agent;
        float messages;

        public MessagesRecord(int agent, long messages) {
            this.agent = agent;
            this.messages = messages;
        }

    }

    public static enum Type {

        BY_AGENT,
        BY_RUNVAR
    }
}
