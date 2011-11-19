/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import bgu.csp.az.api.infra.stat.Statistic;

/**
 *
 * @author bennyl
 */
public class StatisticRoot extends Statistic {

    private String roundName;
    private int indexInRound;

    public StatisticRoot(String roundName, int indexInRound) {
        this.roundName = roundName;
        this.indexInRound = indexInRound;
    }

    public String getRoundName() {
        return roundName;
    }

    public int getIndexInRound() {
        return indexInRound;
    }
    
}
