/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.impl.actions.DirectedMoveAction;
import bgu.dcr.az.vis.player.impl.actions.LocateAction;
import bgu.dcr.az.vis.player.impl.actions.MoveAction;
import bgu.dcr.az.vis.player.impl.actions.RotateAction;
import bgu.dcr.az.vis.tools.Location;

/**
 *
 * @author Zovadi
 */
public class BasicOperationsFrame extends SimpleFrame {

    public BasicOperationsFrame locate(long entityId, Location location) {
        return (BasicOperationsFrame) this.addAction(new LocateAction(entityId, location));
    }

    public BasicOperationsFrame move(long entityId, Location from, Location to) {
        return (BasicOperationsFrame) this.addAction(new MoveAction(entityId, from, to));
    }

    public BasicOperationsFrame directedMove(long entityId, Location from, Location to) {
        return (BasicOperationsFrame) this.addAction(new DirectedMoveAction(entityId, from, to));
    }
    
    public BasicOperationsFrame rotate(long entityId, double fromAngle, double toAngle) {
        return (BasicOperationsFrame) this.addAction(new RotateAction(entityId, fromAngle, toAngle));
    }
}
