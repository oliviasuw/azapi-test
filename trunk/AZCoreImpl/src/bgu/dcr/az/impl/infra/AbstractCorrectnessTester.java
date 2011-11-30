/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.infra;

import bgu.dcr.az.api.infra.CorrectnessTester;

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
