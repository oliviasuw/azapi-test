/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package agents;

import attributes.State;
import agentData.Data;
import java.util.HashMap;
import services.Service;

/**
 *
 * @author Eran
 */
public abstract class Agent {
    protected static int newID = 0;
    
    protected int id;
    protected String currState;
    protected HashMap<String, State> states;
    protected HashMap<Class<? extends Service>, Service> services;
    protected HashMap<Class<? extends Data>, Data> dataMap; //will be created by himself
    
    public Agent(HashMap<Class<? extends Service>, Service> s, HashMap<Class<? extends Data>, Data> d){
        this.id = newID++;
        
        this.services = s;
        this.dataMap = new HashMap<>();
        this.states = new HashMap<>();
    }
    
    public abstract void init();
    
    public int getID(){
        return id;
    }

    public HashMap<Class<? extends Service>, ? extends Service> getServices() {
        return services;
    }

    public HashMap<Class<? extends Data>, ? extends Data> getDataMap() {
        return dataMap;
    }
    
    public void run(){
        if(currState == null || dataMap.isEmpty())
            throw new UnsupportedOperationException(String.format("Agent[%d]::Agent wasn't initialized!", id));
        else if(states.get(currState) == null)
            throw new IllegalArgumentException(String.format("Agent[%d]::No such state '%s'", id, currState));
        else{
            states.get(currState).applyBehaviors();
            changeState();
        }
    }

    public <T extends Data> T getData(Class<T> type){
        T data = (T) this.dataMap.get(type);
        
        if(data == null)
            throw new UnsupportedOperationException(String.format("%s doesn't contain a required data (%s)",getClass().getSimpleName(), type.getSimpleName()) );
        else
            return data;
    }
    
    public <T extends Service> T getService(Class<T> type){
        T service = (T) this.services.get(type);
        
        if(service == null)
            throw new UnsupportedOperationException(String.format("%s doesn't contain a required service (%s)",getClass().getSimpleName(), type.getSimpleName()));
        else
            return service;
    }
    
    public <T extends Data> void addData(Class<T> type, T data){
        if(data == null)
            throw new NullPointerException("Can't add null data!");
        else
            this.dataMap.put(type, data);
    }
    
    public <T extends Service> void addService(Class<T> type, T service){
        if(service == null)
            throw new NullPointerException("Can't add null service!");
        else
            this.services.put(type, service);
    }
    
    protected abstract void changeState();
}
