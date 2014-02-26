/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.actions;

import bgu.dcr.az.vis.player.impl.Location;

/**
 *
 * @author Zovadi
 */
public class LocateAction extends MoveAction {

    public LocateAction(long entityId, Location location) {
        super(entityId, location, location);
    }

}
