/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;

/**
 *
 * @author bennyl
 */
public class ModuleUninstalled {

    private Module module;
    private ModuleContainer container;

    public ModuleUninstalled(Module module, ModuleContainer container) {
        this.module = module;
        this.container = container;
    }

    public ModuleContainer getContainer() {
        return container;
    }

    public Module getModule() {
        return module;
    }

}
