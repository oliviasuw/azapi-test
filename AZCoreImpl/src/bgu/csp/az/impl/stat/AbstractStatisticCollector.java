/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.stat;

import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.stat.DBRecord;
import bgu.csp.az.api.infra.stat.StatisticCollector;
import bgu.csp.az.impl.db.DatabaseUnit;
import bgu.csp.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
public abstract class AbstractStatisticCollector<T extends DBRecord> extends AbstractConfigureable implements StatisticCollector<T> {

    Round round;

    public void setRound(Round round) {
        this.round = round;
    }

    @Override
    public String getName() {
        return "UN NAMED";
    }
    
    
    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }


    @Override
    public void submit(T record) {
        DatabaseUnit.UNIT.insertLater(record, round);
    }
}
