/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

/**
 *
 * @author Administrator
 */
public interface VisualizationDrawer<CANVAS_IMPL, INITIAL_STATE, STATE> {
    
    public void init(CANVAS_IMPL canvas, INITIAL_STATE state);
    public void play(CANVAS_IMPL canvas, STATE state);
    public void rewind(CANVAS_IMPL canvas, STATE state);
    public void fastForward(CANVAS_IMPL canvas, STATE state);
}
