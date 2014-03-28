/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.dcr.api.problems.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator {
    
    void generate(Problem p, Random rand);
}
