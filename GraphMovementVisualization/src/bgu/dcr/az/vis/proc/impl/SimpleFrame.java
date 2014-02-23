/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.ActionSequence;
import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.Frame;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zovadi
 */
public class SimpleFrame implements Frame {

    private final double duration;
    private final Map<Entity, ActionSequence> entitiesActions;

    public SimpleFrame(double duration) {
        this.duration = duration;
        entitiesActions = new HashMap<>();
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public ActionSequence getActionSequence(Entity entity) {
        return entitiesActions.get(entity);
    }
}
