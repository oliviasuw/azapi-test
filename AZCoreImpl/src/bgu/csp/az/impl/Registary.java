/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bc.dsl.JavaDSL;
import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.impl.pgen.ConnectedDCOPGen;
import bgu.csp.az.impl.pgen.UnstracturedADCOPGen;
import bgu.csp.az.impl.pgen.UnstracturedDCOPGen;
import bgu.csp.az.impl.pgen.UnstracturedDCSPGen;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public enum Registary {

    UNIT;
    private List<File> lookupPaths = new LinkedList<File>();
    private Map<ProblemType, Map<String, ProblemGenerator>> problemGenerators = null;

    public void addLookupPath(File path) {
        lookupPaths.add(path);
    }

    void loadProblemGenerators() {

        if (problemGenerators == null) {
            problemGenerators = new HashMap<ProblemType, Map<String, ProblemGenerator>>();
            ProblemGenerator[] defaultGens = {new UnstracturedADCOPGen(), new UnstracturedDCOPGen(), new UnstracturedDCSPGen(), new ConnectedDCOPGen()};
            for (ProblemGenerator d : defaultGens) {
                JavaDSL.innerMap(problemGenerators, d.getType()).put(d.getName(), d);
            }
        }


        /*Reflections ref = new Reflections(new ConfigurationBuilder()
        .addUrls(ClasspathHelper.))*/
        //need to scan to find all instances of problem generator
        //we have to define search directory - can be plugin directory or maybe register code search directory so 
        //the test environment could load the problem generator from the bin folder
    }

    public ProblemGenerator getProblemGenerator(String name, ProblemType type) {
        loadProblemGenerators();
        return JavaDSL.innerMap(problemGenerators, type).get(name);
    }
}
