/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen.info;

import bgu.dcr.az.abm.api.Behavior;

/**
 *
 * @author bennyl
 */
public class BehaviorsChangedInfo {

    private ChangeType changeType;
    private Class<? extends Behavior> changedBehavior;

    public BehaviorsChangedInfo(ChangeType changeType, Class<? extends Behavior> changedBehavior) {
        this.changeType = changeType;
        this.changedBehavior = changedBehavior;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public Class<? extends Behavior> getChangedBehavior() {
        return changedBehavior;
    }

}
