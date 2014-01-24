/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop;

import bgu.dcr.az.anop.algo.AgentManipulator;
import bgu.dcr.az.anop.conf.Configuration;
import java.util.Collection;

/**
 *
 * @author shl
 */
public interface Registery {

    Collection<Class> getImplementors(Class c);

    String getRegisteredClassName(Class c);

    Class getRegisteredClassByName(String registeredName);

    Collection<String> getAllRegisteredNames();

    Configuration getConfiguration(Class c) throws ClassNotFoundException;

    Configuration getConfiguration(String registeration) throws ClassNotFoundException;

    AgentManipulator getAgentManipulator(Class c) throws ClassNotFoundException;
}
