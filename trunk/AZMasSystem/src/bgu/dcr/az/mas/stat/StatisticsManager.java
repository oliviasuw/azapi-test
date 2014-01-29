/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.stat;

import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import java.util.Collection;

/**
 * represents an entity which responsible for all the statistic related
 * resources for a given experiment
 *
 * @author User
 */
public interface StatisticsManager extends ExecutionService {

    /**
     * @return the plotter that is registered for this statistic collector
     */
    Plotter plotter();

    /**
     * @return the statistic database
     */
    EmbeddedDatabaseManager database();

    /**
     * @return the registered statistic collectors
     */
    Collection<StatisticCollector> registered();

    /**
     * register a new statistic collector to be used in the experiment
     *
     * @param stat
     */
    void register(StatisticCollector stat);
}
