/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.pgen;

import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.infra.Configureable;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator extends Configureable{
    public static final String CONFIGURATION_ID = "pgen";
    
    void generate(Problem p, Random rand);
}
