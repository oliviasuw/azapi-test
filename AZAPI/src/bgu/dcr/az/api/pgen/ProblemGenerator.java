/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.pgen;

import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator {
    public static final String CONFIGURATION_ID = "pgen";
    
    void generate(Problem p, Random rand);
}
