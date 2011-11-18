/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.VariableMetadata;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public abstract class AbstractConfigureable implements Configureable{

    private String name;
    private String desc;

    public AbstractConfigureable(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    
    @Override
    public String getConfigurationName() {
        return name;
    }

    @Override
    public String getConfigurationDescription() {
        return desc;
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
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
    public void configure(Map<String, Object> variables) {
        VariableMetadata.assign(this, variables);
        configurationDone();
    }

    protected abstract void configurationDone() ;


}
