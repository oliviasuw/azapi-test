/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.stat;

import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.StatisticAnalyzer;
import bgu.csp.az.api.infra.stat.VisualModel;
import bgu.csp.az.impl.infra.AbstractConfigureable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class DefaultStatisticAnalyzer extends AbstractConfigureable implements StatisticAnalyzer {
    public static final String NCCC_STATISTIC = "NCCC";
    public static final String NCSC_STATISTIC = "NCSC";

    private static final String[] STATISTIC_NAMES = new String[]{NCCC_STATISTIC, NCSC_STATISTIC};
    
    @Override
    public String[] provideAnalyzedStatisticsNames(Database db) {
        return STATISTIC_NAMES;
    }

    @Override
    public VisualModel analyze(String statisticName, Database db) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isApplicable(Round rnd) {
        return true;
    }

    @Override
    protected void configurationDone() {
        // DONT CARE :)
    }

}
