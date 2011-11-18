/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pgen;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
public abstract class AbstractProblemGenerator extends AbstractConfigureable implements ProblemGenerator {

    protected String name;
    protected ProblemType type;

    public AbstractProblemGenerator(String name, ProblemType type) {
        super("pgen", "problem generator");
        this.name = name;
        this.type = type;
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProblemType getType() {
        return type;
    }

    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
}
