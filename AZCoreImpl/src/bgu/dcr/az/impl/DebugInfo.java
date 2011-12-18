/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.infra.Configureable;
import bgu.dcr.az.impl.infra.AbstractConfigureable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
@Register(name = "debug-info", display="Debugging Information", visible=false)
public class DebugInfo extends AbstractConfigureable {
    @Variable(name="round-name", description="failing round name")
    String roundName = "";
    @Variable(name="algorithm-name", description="failing algorithm name")
    String algName = "";
    @Variable(name="name", description="identifier for this debug info")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="number", description="number of the failing problem")
    int number = -1;
    
    List<VarAssign> pgenVars = new LinkedList<VarAssign>();

    public DebugInfo(String roundName, String algName, int number) {
        this.roundName = roundName;
        this.algName = algName;
        this.number = number;
    }

    public DebugInfo() {
    }
    
    public String getRoundName(){
        return roundName;
    }
    public String getAlgorithmName(){
        return algName;
    }
    
    public int getFailedProblemNumber(){
        return number;
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
