/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.mas;

import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author User
 */
public interface ExecutionService {
    void initialize(Execution ex) throws InitializationException;
}
