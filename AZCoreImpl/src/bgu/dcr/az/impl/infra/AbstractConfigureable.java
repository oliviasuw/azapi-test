/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.infra;

import bc.dsl.JavaDSL;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.infra.Configureable;
import bgu.dcr.az.api.infra.VariableMetadata;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public abstract class AbstractConfigureable implements Configureable{

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        return Collections.emptyList();
    }

    @Override
    public List<Configureable> getConfiguredChilds() {
        return Collections.emptyList();
    }
    
    

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        return false;
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        throw new InvalidValueException("no expected sub configurations");
    }

    @Override
    public void bubbleDownVariable(String var, Object val) {
        configure(JavaDSL.cassoc(var, val));
    }

    @Override
    public void configure(Map<String, Object> variables) {
        VariableMetadata.assign(this, variables);
        configurationDone();
    }

    protected abstract void configurationDone() ;


}
