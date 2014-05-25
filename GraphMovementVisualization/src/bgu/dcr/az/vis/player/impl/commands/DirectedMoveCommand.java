/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.commands;

import bgu.dcr.az.vis.player.api.Command;
import bgu.dcr.az.vis.tools.Location;

/**
 *
 * @author Zovadi
 */
public class DirectedMoveCommand extends MoveCommand {

    private final double angle;

    public DirectedMoveCommand(long entityId, Location from, Location to) {
        super(entityId, from, to);
        angle = 180 * Math.atan2(to.getY() - from.getY(), to.getX() - from.getX()) / Math.PI;
    }

    @Override
    protected void _update(double percentage) {
        getEntity().rotationProperty().set(angle);
        super._update(percentage);
    }

//    @Override
//    public Command subAction(double percentageFrom, double percentageTo) {
//        double dx = to.getX() - from.getX();
//        double dy = to.getY() - from.getY();
//
//        return new DirectedMoveAction(getEntityId(),
//                new Location(from.getX() + dx * percentageFrom, from.getY() + dy * percentageFrom),
//                new Location(from.getX() + dx * percentageTo, from.getY() + dy * percentageTo));
//    }

}
