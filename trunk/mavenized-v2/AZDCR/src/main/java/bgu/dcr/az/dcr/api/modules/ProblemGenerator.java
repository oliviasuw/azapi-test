/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.execs.exps.exe.Simulation;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator extends Module<Simulation<CPData, CPSolution>>{
    
    void generate(Problem p, Random rand);
}
