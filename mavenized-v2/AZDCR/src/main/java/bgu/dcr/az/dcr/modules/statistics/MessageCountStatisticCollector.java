/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.conf.api.Variable;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.statistics.info.ExecutionTerminationInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author bennyl
 */
@Register("msgc-sc")
public class MessageCountStatisticCollector extends AbstractStatisticCollector {

    @Variable(name = "type", description = "type of the graph to show (BY_AGENT/BY_RUNVAR)", defaultValue = "BY_RUNVAR")
    Type graphType = Type.BY_RUNVAR;

    @Override
    public String getName() {
        return "Message Count";
    }

    @Override
    protected void plot(QueryDatabase database, CPExperimentTest test) {
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
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable("MESSAGE_COUNT", Record.class);

        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            for (int i = 0; i < ex.data().getMessagesCount().length; i++) {
                write(new Record(i, ex.data().getMessagesCount()[i]));
            }
        });
    }

    public static class Record extends CPRecord {

        int agent;
        float messages;

        public Record(int agent, long messages) {
            this.agent = agent;
            this.messages = messages;
        }

    }

    public static enum Type {

        BY_AGENT,
        BY_RUNVAR
    }
}
