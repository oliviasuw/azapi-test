/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.impl.Location;

/**
 *
 * @author Zovadi
 */
public class DirectedMoveAction extends MoveAction {

    private final double angle;

    public DirectedMoveAction(long entityId, Location from, Location to) {
        super(entityId, from, to);
        angle = 180 * Math.atan2(to.getY() - from.getY(), to.getX() - from.getX()) / Math.PI;
    }

    @Override
    protected void _update() {
        getEntity().rotationProperty().set(angle);
        super._update();
    }

    @Override
    public Action subAction(double percentageFrom, double percentageTo) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();

        return new DirectedMoveAction(getEntityId(),
                new Location(from.getX() + dx * percentageFrom, from.getY() + dy * percentageFrom),
                new Location(from.getX() + dx * percentageTo, from.getY() + dy * percentageTo));
    }

}
