/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.conf.modules.Module;

/**
 *
 * @author User
 */
public interface Looper extends Module{

    int count();

    void configure(int i, Object o);
    
    String getRunningVariableName();
    
    double getRunningVariableValue(int i);
}
