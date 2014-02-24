/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.api.Frame;
import java.util.ArrayList;
import java.util.Iterator;

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
    public void addAction(Action action) {
        actions.add(action);
    }

    @Override
    public Iterator<Action> iterator() {
        return actions.iterator();
    }

}
