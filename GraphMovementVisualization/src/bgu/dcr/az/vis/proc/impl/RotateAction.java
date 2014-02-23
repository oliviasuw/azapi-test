/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.ActionSequence;
import bgu.dcr.az.vis.proc.api.Entity;
import java.util.Arrays;
import java.util.Collection;
import javafx.animation.KeyValue;

/**
 *
 * @author Zovadi
 */
public class RotateAction extends SimpleAction {

    private final double angle;

    public RotateAction(double angle, double duration) {
        super(duration);
        this.angle = angle;
    }

    @Override
    public Collection<KeyValue> apply(Entity entity) {
        return Arrays.asList(new KeyValue(entity.rotationProperty(), entity.rotationProperty().get() + angle));
    }

    @Override
    public ActionSequence split(double duration) {
        SimpleActionSequence sas = new SimpleActionSequence();

        if (duration > getDurationFraction()) {
            sas.addAction(this);
        } else {
            double splitLevel = duration / getDurationFraction();
            sas.addAction(new RotateAction(angle * splitLevel, duration))
               .addAction(new RotateAction(angle * (1 - splitLevel), getDurationFraction() - duration));
        }

        return sas;
    }
}
