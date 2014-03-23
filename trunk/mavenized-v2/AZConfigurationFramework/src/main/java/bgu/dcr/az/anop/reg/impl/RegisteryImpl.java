/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.reg.impl;

import bgu.dcr.az.anop.reg.RegisteryAnnotationProcessor;
import bgu.dcr.az.anop.alg.AlgorithmAnnotationProcessor;
import bgu.dcr.az.anop.algo.AgentManipulator;
import bgu.dcr.az.anop.conf.Configuration;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 *
 * @author User
 */
public class RegisteryImpl implements bgu.dcr.az.anop.reg.Registery {

    private final BiMap<String, Class> registeredClassesByName = HashBiMap.create();
    private final Map<Class, Set<Class>> classExtenders = new HashMap<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public RegisteryImpl() {

        //load classes
        System.err.println("Scanning registery");
        //ServiceLoader<Registration> services = ServiceLoader.load(Registration.class);

        Reflections ref = new Reflections("bgu.dcr.autogen.registry", new SubTypesScanner());
        Set<Class<? extends Registration>> services = ref.getSubTypesOf(Registration.class);

        for (Class<? extends Registration> service : services) {
            try {
                ((Registration) service.newInstance()).register(this);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(RegisteryImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.err.println("Done Scanning");
    }

    public void register(Class clazz, String registeredName) {
        System.err.println("Class " + clazz.getCanonicalName() + " is registered with name: " + registeredName);
        if (registeredClassesByName.put(registeredName, clazz) != null) {
            throw new UnsupportedOperationException("Registered name must be unique: " + registeredName + " (try running clean and build)");
        }

        LinkedList<Class> openList = new LinkedList<>();
        openList.add(clazz);
        while (!openList.isEmpty()) {
            Class c = openList.removeFirst();
            for (Class<?> ifc : c.getInterfaces()) {
                putExtender(ifc, c);
                openList.add(ifc);
            }

            if (c != Object.class && !c.isInterface()) {
                putExtender(c.getSuperclass(), c);
                openList.add(c.getSuperclass());
            }
        }

    }

    @Override
    public Collection<Class> getImplementors(Class c) {
        Set<Class> result = new HashSet<>();

        LinkedList<Class> openList = new LinkedList<>();
        openList.add(c);
        while (!openList.isEmpty()) {
            Class next = openList.removeFirst();
            if (!((next.isMemberClass() && !Modifier.isStatic(next.getModifiers())) || next.isInterface() || java.lang.reflect.Modifier.isAbstract(next.getModifiers()))) {
                if (registeredClassesByName.inverse().containsKey(next)) {
                    result.add(next);
                }
            }

            Set<Class> got = classExtenders.get(next);
            if (got != null) {
                openList.addAll(got);
            }
        }

        return result;
    }

    private void putExtender(Class<?> ifc, Class<? extends Object> c) {
        Set<Class> iset = classExtenders.get(ifc);
        if (iset == null) {
            iset = new HashSet<>();
            classExtenders.put(ifc, iset);
        }

        iset.add(c);
    }

    @Override
    public String getRegisteredClassName(Class c) {
        return registeredClassesByName.inverse().get(c);
    }

    @Override
    public Class getRegisteredClassByName(String registeredName) {
        return registeredClassesByName.get(registeredName);
    }

    @Override
    public Collection<String> getAllRegisteredNames() {
        return registeredClassesByName.keySet();
    }

    @Override
    public Configuration getConfiguration(Class c) throws ClassNotFoundException {
        if (c == null) {
            return null;
        }

        try {
            return (Configuration) Class.forName(RegisteryAnnotationProcessor.AUTOGEN_PACKAGE + "." + c.getCanonicalName().replace('.', '_')).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ClassNotFoundException("class with the given name exsits but not satisfying the needed contract", ex);
        }
    }

    @Override
    public Configuration getConfiguration(String registeration) throws ClassNotFoundException {
        return getConfiguration(getRegisteredClassByName(registeration));
    }

    @Override
    public AgentManipulator getAgentManipulator(Class c) throws ClassNotFoundException {
        if (c == null) {
            return null;
        }

        try {
            return (AgentManipulator) Class.forName(AlgorithmAnnotationProcessor.AUTOGEN_PACKAGE + "." + c.getCanonicalName().replace('.', '_')).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ClassNotFoundException("class with the given name exsits but not satisfying the needed contract", ex);
        }
    }

}
