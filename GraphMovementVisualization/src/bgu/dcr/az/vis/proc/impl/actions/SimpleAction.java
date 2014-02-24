/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.api.VisualScene;

/**
 *
 * @author Zovadi
 */
public abstract class SimpleAction implements Action {

    private double currentTime;
    private final double duration;

    public SimpleAction(double duration) {
        this.duration = duration;
        this.currentTime = 0;
    }

//    @Override
    public double getDuration() {
        return duration;
    }

    public double getCurrentTime() {
        return currentTime;
    }
    
//    @Override
    public final void init(VisualScene scene) {
        currentTime = 0;
        _init(scene);
    }

    protected abstract void _init(VisualScene scene);

//    @Override
    public final void tick(VisualScene scene, double duration) {
        _tick(scene, duration);
        currentTime += duration;
    }

    protected abstract void _tick(VisualScene scene, double duration);

//    @Override
    public final boolean isFinished() {
        return currentTime >= duration;
    }
}
