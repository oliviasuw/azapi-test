/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.registery.Register;

/**
 *
 * @author bennyl
 */
@Register("test-module1")
public class TestModule1 implements Module {

    boolean initialized = false;

    @Override
    public void initialize(ModuleContainer mc) {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

}
