/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Configureable;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author bennyl
 */
public enum Registary {

    UNIT;
    Map<String, Class> registeredXMLEntities = new HashMap<String, Class>();
    Map<String, Class> agents = new HashMap<String, Class>();

    private Registary() {

        //Reflections ref = new Reflections("bgu.csp.az", new TypeAnnotationsScanner());

        Reflections ref = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("bgu.dcr.az"), ClasspathHelper.forPackage("ext.sim")).setScanners(new TypeAnnotationsScanner()));

        //SCANNING XML ENTITIES
        Set<Class<?>> types = ref.getTypesAnnotatedWith(Register.class);
        for (Class<?> type : types) {
            if (type.isInterface() || type.isAnonymousClass() || Modifier.isAbstract(type.getModifiers())) {
                System.out.println("Found Abstract Registered Item - ignoring it: " + type.getSimpleName());
            } else {
                final String name = type.getAnnotation(Register.class).name();
                System.out.println("Found Registered item : " + type.getSimpleName() + " as " + name);
                registeredXMLEntities.put(name, type);
            }
        }

        //SCANNING AGENTS
        types = ref.getTypesAnnotatedWith(Algorithm.class);
        for (Class<?> type : types) {
            if (type.isInterface() || type.isAnonymousClass() || Modifier.isAbstract(type.getModifiers())) {
                System.out.println("Found Abstract Agent - ignoring it: " + type.getSimpleName());
            } else {
                final String name = type.getAnnotation(Algorithm.class).name();
                System.out.println("Found Agent : " + type.getSimpleName() + " as " + name);
                agents.put(name, type);
            }
        }
    }

    public Class getXMLEntity(String type) {
        return registeredXMLEntities.get(type);
    }

    Class<? extends Agent> getAgentByAlgorithmName(String name) {
        return agents.get(name);
    }

    public String getEntityName(Configureable conf) {
        return conf.getClass().getAnnotation(Register.class).name();
    }
}
