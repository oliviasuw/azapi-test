/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pgen;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.impl.infra.AbstractConfigureable;
import java.util.Map;
import java.util.Random;

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
    public void generate(Map<String, Object> variables, Problem p, Random rand) {
        VariableMetadata.assign(this, variables);
        _generate(p, rand);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProblemType getType() {
        return type;
    }

    protected abstract void _generate(Problem p, Random rand);

    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
}
