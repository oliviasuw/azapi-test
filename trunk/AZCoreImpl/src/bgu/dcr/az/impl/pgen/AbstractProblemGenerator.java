/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.pgen;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.api.pgen.ProblemGenerator;
import bgu.dcr.az.impl.infra.AbstractConfigurable;

/**
 *
 * @author bennyl
 */
public abstract class AbstractProblemGenerator extends AbstractConfigurable implements ProblemGenerator{

    
    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
}
