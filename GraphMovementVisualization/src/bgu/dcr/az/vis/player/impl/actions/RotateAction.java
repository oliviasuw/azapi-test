/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.actions;

import bgu.dcr.az.vis.player.api.Action;
import bgu.dcr.az.vis.tools.easing.DoubleEasingVariable;
import bgu.dcr.az.vis.tools.easing.EasingVariableDoubleBased;
import bgu.dcr.az.vis.tools.easing.LinearDouble;

/**
 *
 * @author Zovadi
 */
public class RotateAction extends SingleEntityAction {

    private final DoubleEasingVariable angleEasingVar;
    private final double fromAngle;
    private final double toAngle;

    public RotateAction(long entityId, double fromAngle, double toAngle) {
        super(entityId);
        this.fromAngle = fromAngle;
        this.toAngle = toAngle;
        angleEasingVar = new DoubleEasingVariable(new LinearDouble(), EasingVariableDoubleBased.EasingFunctinTypeDouble.EASE_IN, fromAngle, toAngle);
    }

    @Override
    public void _initialize(long transitionMillis) {
        angleEasingVar.change(toAngle, transitionMillis);
    }
    
    @Override
    protected void _update(double percentage) {
        angleEasingVar.update(percentage);
        
        getEntity().rotationProperty().set(angleEasingVar.getCurrentValue());
    }

    @Override
    public Action subAction(double percentageFrom, double percentageTo) {
        double dr = toAngle - fromAngle;

        return new RotateAction(getEntityId(), fromAngle + dr * percentageFrom, fromAngle + dr * percentageTo);
    }
}