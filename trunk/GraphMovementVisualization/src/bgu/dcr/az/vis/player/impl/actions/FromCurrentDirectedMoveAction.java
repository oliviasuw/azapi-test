/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.actions;

import bgu.dcr.az.vis.player.impl.Location;
import bgu.dcr.az.vis.tools.easing.DoubleEasingVariable;
import bgu.dcr.az.vis.tools.easing.EasingVariableDoubleBased;
import bgu.dcr.az.vis.tools.easing.LinearDouble;

/**
 *
 * @author Shlomi
 */
public class FromCurrentDirectedMoveAction extends DirectedMoveAction {

    public FromCurrentDirectedMoveAction(long entityId, Location to) {
        super(entityId, new Location(), to);
    }

    @Override
    public void _initialize() {
        super._initialize();
        this.from.xProperty().set(getEntity().locationProperty().getValue().getX());
        this.from.yProperty().set(getEntity().locationProperty().getValue().getY());

    }

}
