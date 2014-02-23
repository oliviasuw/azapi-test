/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.api.ActionSequence;
import java.util.LinkedList;

/**
 *
 * @author Zovadi
 */
public class SimpleActionSequence implements ActionSequence {

    private final LinkedList<Action> actions;

    public SimpleActionSequence() {
        actions = new LinkedList<>();
    }

    @Override
    public ActionSequence addAction(Action action) {
        actions.addLast(action);
        return this;
    }

    public ActionSequence locateAt(Location location) {
        addAction(new LocateAction(location));
        return this;
    }

    public ActionSequence moveTo(Location location, double duration) {
        addAction(new MoveAction(location, duration));
        return this;
    }

    public ActionSequence rotate(double angle, double duration) {
        addAction(new RotateAction(angle, duration));
        return this;
    }

    @Override
    public ActionSequence addActionSequence(ActionSequence sequence) {
        for (int i = 0; sequence.getFirstAction() != null; i++) {
            Action action = sequence.getFirstAction();
            actions.add(i, action);
            sequence.advance(action.getDurationFraction());
        }

        return this;
    }

    @Override
    public Double getNextPOI() {
        return actions.isEmpty() ? null : actions.getFirst().getDurationFraction();
    }

    @Override
    public ActionSequence advance(double duration) {
        while (!actions.isEmpty() && getNextPOI() >= duration) {
            Action action = actions.remove();
            duration -= action.getDurationFraction();
        }

        if (!actions.isEmpty() && duration > 0) {
            Action action = actions.remove();
            addActionSequence(action.split(duration));
        }

        return this;
    }

    @Override
    public Action getFirstAction() {
        return actions.isEmpty() ? null : actions.getFirst();
    }
}
