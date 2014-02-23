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
public class MoveAction extends SimpleAction {

    private final Location destination;

    public MoveAction(Location destination, double duration) {
        super(duration);
        this.destination = destination;
    }

    @Override
    public Collection<KeyValue> apply(Entity entity) {
        return Arrays.asList(
                new KeyValue(entity.locationProperty().get().xProperty(), destination.getX()),
                new KeyValue(entity.locationProperty().get().yProperty(), destination.getY())
        );
    }

    @Override
    public ActionSequence split(double duration) {
        SimpleActionSequence sas = new SimpleActionSequence();

        if (duration > getDurationFraction()) {
            sas.addAction(this);
        } else {
            //TODO: perform movement action split
        }

        return sas;
    }
}
