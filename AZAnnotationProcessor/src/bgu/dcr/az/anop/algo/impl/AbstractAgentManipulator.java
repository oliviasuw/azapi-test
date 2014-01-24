/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.algo.impl;

import bgu.dcr.az.anop.algo.AgentManipulator;
import bgu.dcr.az.anop.algo.HandlerInfo;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.TypeInfo;
import bgu.dcr.az.anop.conf.VisualData;
import bgu.dcr.az.anop.conf.impl.AbstractConfiguration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author User
 */
public abstract class AbstractAgentManipulator implements AgentManipulator, Configuration {

    protected List<HandlerInfo> handlers;
    protected String algorithmName;
    protected Configuration configurationDelegate;

    @Override
    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public Collection<HandlerInfo> handlers() {
        return handlers;
    }

    @Override
    public Collection<Property> properties() {
        return configurationDelegate.properties();
    }

    @Override
    public TypeInfo typeInfo() {
        return configurationDelegate.typeInfo();
    }

    @Override
    public VisualData visualData() {
        return configurationDelegate.visualData();
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

}
