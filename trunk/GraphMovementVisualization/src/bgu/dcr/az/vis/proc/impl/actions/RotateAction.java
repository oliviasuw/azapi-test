/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.VisualScene;
import javafx.animation.KeyValue;

/**
 *
 * @author Zovadi
 */
public class RotateAction extends SingleEntityAction {

    private final double finalAngle;
    private double initialAngle;

    public RotateAction(long entityId, double finalAngle, double duration) {
        super(entityId, duration);
        this.finalAngle = finalAngle;
    }

    @Override
    protected void _init(VisualScene scene) {
        Entity entity = scene.getEntity(getEntityId());
        initialAngle = entity.rotationProperty().get();
    }

    @Override
    protected void _tick(VisualScene scene, double duration) {
        Entity entity = scene.getEntity(getEntityId());
        double percentage = getCurrentTime() / getDuration();
        double ta = (finalAngle - initialAngle) * percentage + initialAngle;
        scene.addDelayedTransformation(new KeyValue(entity.rotationProperty(), ta));
    }

    @Override
    public void execute(VisualScene scene) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
