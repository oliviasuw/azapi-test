/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.correctness;

import bgu.dcr.az.api.infra.CorrectnessTester;
import bgu.dcr.az.impl.infra.AbstractConfigureable;

/**
 *
 * @author bennyl
 */
public abstract class AbstractCorrectnessTester extends AbstractConfigureable implements CorrectnessTester{

    @Override
    protected void configurationDone() {
        //DONT CARE :)
    }
    
}
