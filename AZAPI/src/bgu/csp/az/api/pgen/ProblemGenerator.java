/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.pgen;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.infra.Configureable;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator extends Configureable{
    public static final String CONFIGURATION_ID = "pgen";
    
    void generate(Map<String, Object> variables, Problem p, Random rand);
    String getName();
    ProblemType getType();
}
