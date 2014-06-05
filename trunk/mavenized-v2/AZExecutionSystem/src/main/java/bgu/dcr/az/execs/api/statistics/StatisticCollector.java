/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.statistics;

import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.execs.exps.ExecutionTree;

/**
 *
 * @author User
 */
public interface StatisticCollector extends Module<Test> {

    String getName();

    void plot(Plotter ploter, Test test);
}
