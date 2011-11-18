/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.stat;

import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.StatisticAnalyzer;
import bgu.csp.az.api.infra.stat.VisualModel;
import bgu.csp.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
public abstract class AbstractStatisticAnalyzer extends AbstractConfigureable implements StatisticAnalyzer {

    private String statisticName;

    public AbstractStatisticAnalyzer(String statisticName) {
        super("stat", "statistic analayzer definition");
        this.statisticName = statisticName;
    }

    @Override
    public String[] provideAnalyzedStatisticsNames(Database db) {
        return new String[]{statisticName};
    }

    @Override
    public VisualModel analyze(String statisticName, Database db) {
        return _analyze(db);
    }

    protected abstract VisualModel _analyze(Database db);

    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
}
