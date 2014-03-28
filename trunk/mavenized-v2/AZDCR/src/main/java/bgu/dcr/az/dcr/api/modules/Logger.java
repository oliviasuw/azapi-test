/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.execs.api.experiments.ExecutionService;

/**
 *
 * @author User
 */
public interface Logger extends ExecutionService<CPData> {

    void log(String logger, String msg);
}
