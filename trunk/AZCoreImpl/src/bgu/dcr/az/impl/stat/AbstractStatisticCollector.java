/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.stat;

import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.api.infra.stat.DBRecord;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.impl.db.DatabaseUnit;
import bgu.dcr.az.impl.infra.AbstractConfigureable;

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
    protected void configurationDone() {
        //DONT CARE :)
    }


    @Override
    public void submit(T record) {
        DatabaseUnit.UNIT.insertLater(record, round);
    }
}
