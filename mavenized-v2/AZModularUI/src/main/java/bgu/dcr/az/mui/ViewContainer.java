/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.ModuleContainer;

/**
 *
 * @author bennyl
 */
public class ViewContainer extends ModuleContainer {

    public ViewContainer() {
    }

    public ViewContainer(ViewContainer parent) {
        setParent(parent);
    }

    public View getView() {
        return get(View.class);
    }
}
