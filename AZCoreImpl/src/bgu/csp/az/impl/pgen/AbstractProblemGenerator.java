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

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }


    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
}
