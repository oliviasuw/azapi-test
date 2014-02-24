/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public interface EasingFunctionDoubleBased {

    /**
     *
     * Example: time= 0; double beginning= 0; double change = 390; double
     * duration = 200;
     *
     * @param t time
     * @param b beginning
     * @param c change
     * @param d duration
     * @return
     */
    double easeIn(double t, double b, double c, double d);

    double easeInOut(double t, double b, double c, double d);

    double easeOut(double t, double b, double c, double d);
}
