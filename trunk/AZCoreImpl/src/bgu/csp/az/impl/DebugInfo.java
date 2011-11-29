/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.impl.infra.AbstractConfigureable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
@Register(name = "debug-info")
public class DebugInfo extends AbstractConfigureable {
    @Variable(name="round-name", description="failing round name")
    String roundName = "";
    @Variable(name="algorithm-name", description="failing algorithm name")
    String algName = "";
    @Variable(name="name", description="identifier for this debug info")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="seed", description="seed of the failing problem")
    long seed = -1;
    
    List<VarAssign> pgenVars = new LinkedList<VarAssign>();

    public DebugInfo(String roundName, String algName, long seed) {
        this.roundName = roundName;
        this.algName = algName;
        this.seed = seed;
    }

    public DebugInfo() {
    }
    
    public String getRoundName(){
        return roundName;
    }
    public String getAlgorithmName(){
        return algName;
    }
    public long getProblemSeed(){
        return seed;
    }
    public List<VarAssign> getProblemGeneratorConfiguration(){
        return pgenVars;
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        return Arrays.<Class<? extends Configureable>>asList(VarAssign.class);
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        return VarAssign.class.isAssignableFrom(cls);
    }

    @Override
    public List<Configureable> getConfiguredChilds() {
        return new LinkedList<Configureable>(pgenVars);
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (canAccept(sub.getClass())){
            pgenVars.add((VarAssign)sub);
        }else {
            throw new InvalidValueException("Debug info can't accept "+ sub.getClass().getSimpleName());
        }
    }

    @Override
    protected void configurationDone() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        return name;
    }
}
