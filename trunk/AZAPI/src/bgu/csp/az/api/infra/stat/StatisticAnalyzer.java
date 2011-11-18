/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.VariableMetadata;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public interface StatisticAnalyzer extends Configureable{
    
    /**
     * @return the available statistics names that this analyzer can analyze from the given DB
     */
    String[] provideAnalyzedStatisticsNames(Database db);
    
    /**
     * perform the actual analyzing 
     * @param statisticName
     * @param variables
     * @param db
     * @return a visual model containing the analyzed data
     */
    VisualModel analyze(String statisticName, Database db);
    
    /**
     * @param ex the execution 
     * @return true if this statistic analayzer can be applied on the given Round
     */
    boolean isApplicable(Round rnd);
}
