/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution.manipulators;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.execs.sim.Agt0DSL;
import bgu.dcr.az.dcr.api.CPAgent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public abstract class AgentManipulator implements Configuration {

    protected List<HandlerInfo> handlers = new LinkedList<>();
    protected String algorithmName;
    protected Configuration configurationDelegate;

    public String getAlgorithmName() {
        return algorithmName;
    }

    public Collection<HandlerInfo> handlers() {
        return handlers;
    }

    @Override
    public Collection<Property> properties() {
        return configurationDelegate.properties();
    }

    @Override
    public Class configuredType(){
        return configurationDelegate.configuredType();
    }

    public void handle(CPAgent a, String messageName, Object[] arguments){
        Agt0DSL.panic("no such message: " + messageName + " defined for agent " + a.getClass().getSimpleName());
    }
    
    @Override
    public <T> T create() throws ConfigurationException {
        return configurationDelegate.create();
    }

    @Override
    public void configure(Object o) throws ConfigurationException {
        configurationDelegate.configure(o);
    }

    @Override
    public Property get(String name) {
        return configurationDelegate.get(name);
    }

    @Override
    public Iterator<Property> iterator() {
        return configurationDelegate.iterator();
    }

    @Override
    public JavaDocInfo doc() {
        return configurationDelegate.doc();
    }

    @Override
    public Configuration loadFrom(Object o) throws ConfigurationException {
        return configurationDelegate.loadFrom(o);
    }

    @Override
    public String registeredName() {
        return configurationDelegate.registeredName();
    }

    public static AgentManipulator lookup(Class<? extends CPAgent> type){
        try {
            final Class clazz = Class.forName("bgu.dcr.az.dcr.autogen." + (type.getCanonicalName().replaceAll("\\.", "_")));
            return (AgentManipulator) clazz.newInstance();
        } catch (Exception ex) {
            Logger.getLogger(AgentManipulator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
