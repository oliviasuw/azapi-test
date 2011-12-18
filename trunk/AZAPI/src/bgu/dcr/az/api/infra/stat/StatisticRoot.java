/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra.stat;

import bgu.dcr.az.api.infra.stat.Statistic;

/**
 *
 * @author bennyl
 */
public class StatisticRoot extends Statistic {

    private String testName;
    private int indexInTest;

    public StatisticRoot(String testName, int indexInTest) {
        this.testName = testName;
        this.indexInTest = indexInTest;
    }

    public String getTestName() {
        return testName;
    }

    public int getIndexInTest() {
        return indexInTest;
    }
    
}
