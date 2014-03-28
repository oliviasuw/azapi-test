/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;

/**
 *
 * @author User
 */
public interface Looper {

    int count();

    void configure(int i, Configuration[] configurations) throws ConfigurationException;
    
    String getRunningVariableName();
    
    double getRunningVariableValue(int i);
}
