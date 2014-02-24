/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public interface EasingFunction {

    /**
     *
     * Example: int time= 0; float beginning= 0; float change = 390; float
     * duration = 200;
     *
     * @param t time
     * @param b beginning
     * @param c change
     * @param d duration
     * @return
     */
    float easeIn(float t, float b, float c, float d);

    float easeInOut(float t, float b, float c, float d);

    float easeOut(float t, float b, float c, float d);
}
