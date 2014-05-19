/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.statistics;

import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.experiments.Experiment;

/**
 *
 * @author User
 * @param <T>
 */
public interface StatisticCollector<T> extends ExecutionService<T> {

    String getName();

    void plot(Plotter ploter, Experiment experiment);
}
