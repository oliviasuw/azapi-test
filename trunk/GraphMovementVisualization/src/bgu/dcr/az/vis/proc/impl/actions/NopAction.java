/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.VisualScene;

/**
 *
 * @author Shl
 */
public class NopAction extends SimpleAction {

    public NopAction(double duration) {
        super(duration);
    }

    @Override
    protected void _init(VisualScene scene) {
    }

    @Override
    protected void _tick(VisualScene scene, double duration) {
    }    

    @Override
    public void execute(VisualScene scene) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
