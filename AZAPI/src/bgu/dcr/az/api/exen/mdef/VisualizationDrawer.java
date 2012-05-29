/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.Frame;

/**
 *
 * @author Administrator
 */
public interface VisualizationDrawer<IMAGE_IMPL, CANVAS_IMPL, STATE> {
    
    public void init(CANVAS_IMPL canvas, Execution ex);
    /**
     * if this method return false then that means that it didnt handle the given frame and this frame will be 
     * regiven to it on a later time.
     * @param canvas
     * @param state
     * @return 
     */
    public boolean play(CANVAS_IMPL canvas, Frame<STATE> state);
    public void rewind(CANVAS_IMPL canvas, STATE state);
    public void fastForward(CANVAS_IMPL canvas, STATE state);
    public IMAGE_IMPL getThumbnail();
    public String getDescriptionURL();
}
