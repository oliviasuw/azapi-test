/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.api.Frame;
import bgu.dcr.az.vis.proc.api.Player;
import java.util.ArrayList;

/**
 *
 * @author Zovadi
 */
public class SimpleFrame implements Frame {

    private final ArrayList<Action> actions;

    public SimpleFrame() {
        this.actions = new ArrayList<>();
    }

    @Override
    public SimpleFrame addAction(Action action) {
        actions.add(action);
        return this;
    }
    
    @Override
    public void initialize(Player player) {
        actions.parallelStream().forEach(a -> a.initialize(player));
    }

    @Override
    public void update() {
//        actions.parallelStream().forEach(a -> a.update());
        actions.forEach(a -> a.update());
    }

}
