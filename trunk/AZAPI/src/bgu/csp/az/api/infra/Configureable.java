/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

import bgu.csp.az.api.exp.InvalidValueException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * represents a configurable item
 * this is an item that can be configured via out side units like gui or xml
 * the item contains enough metadata information to explain the outer module user
 * how to be configure - every class that implement this type should have 
 * empty parameter constractor - so it can first build and then get configured.
 * 
 * @author bennyl
 */
public interface Configureable{

    /**
     * @return the expected variables 
     */
    VariableMetadata[] provideExpectedVariables();
    /**
     * @return the sub configurations types that this class expecting to get
     * this is not mean that the class expecting one configuration for each type
     * or that it expecting all the types - you should call the canAccept method in betweens any 
     * addSubConfiguration call to ensure that you can add the next configuration
     */
    List<Class<? extends Configureable>> provideExpectedSubConfigurations();
    
    void bubbleDownVariable(String var, Object val);
    
    /**
     * @param cls
     * @return true if this configureable item can accept the given type
     */
    boolean canAccept(Class<? extends Configureable> cls);
    
    /**
     * add new sub configuration if ! canAccept(sub.class) throws InvalidValueException
     * @param sub 
     * @throws InvalidValueException
     */
    void addSubConfiguration(Configureable sub) throws InvalidValueException;
    
    
    /**
     * configure the class variables, 
     * it must be guerentied that first all the subConfigurations has been added 
     * and only then this function will be called
     * @param variables 
     */
    void configure(Map<String, Object> variables);   
    
    List<Configureable> getConfiguredChilds();
}
