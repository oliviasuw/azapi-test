/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.commands;

import bgu.dcr.az.vis.tools.Location;

/**
 *
 * @author Zovadi
 */
public class LocateCommand extends MoveCommand {

    public LocateCommand(long entityId, Location location) {
        super(entityId, location, location);
    }

}
