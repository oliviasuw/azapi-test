/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.impl.commands.DirectedMoveCommand;
import bgu.dcr.az.vis.player.impl.commands.LocateCommand;
import bgu.dcr.az.vis.player.impl.commands.MoveCommand;
import bgu.dcr.az.vis.player.impl.commands.RotateCommand;
import bgu.dcr.az.vis.tools.Location;

/**
 *
 * @author Zovadi
 */
public class BasicOperationsFrame extends SimpleFrame {

    public BasicOperationsFrame locate(long entityId, Location location) {
        return (BasicOperationsFrame) this.addCommand(new LocateCommand(entityId, location));
    }

    public BasicOperationsFrame move(long entityId, Location from, Location to) {
        return (BasicOperationsFrame) this.addCommand(new MoveCommand(entityId, from, to));
    }

    public BasicOperationsFrame directedMove(long entityId, Location from, Location to) {
        return (BasicOperationsFrame) this.addCommand(new DirectedMoveCommand(entityId, from, to));
    }
    
    public BasicOperationsFrame rotate(long entityId, double fromAngle, double toAngle) {
        return (BasicOperationsFrame) this.addCommand(new RotateCommand(entityId, fromAngle, toAngle));
    }
}
