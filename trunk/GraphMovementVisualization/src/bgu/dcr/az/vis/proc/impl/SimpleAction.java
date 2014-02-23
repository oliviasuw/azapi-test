/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Action;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleAction implements Action {

    private double duration;

    public SimpleAction(double duration) {
        this.duration = duration;
    }
    
    @Override
    public double getDurationFraction() {
        return duration;
    }
}
