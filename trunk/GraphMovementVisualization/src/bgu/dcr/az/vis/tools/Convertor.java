/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.tools;

/**
 *
 * @author Shl
 */
public class Convertor {
    /**
     * Translates world coordinate to a view coordinate.
     * Our assumption that zero world coordinate always
     * mapped to zero view coordinate.
     * @param p
     * @param scale
     * @return 
     */
    public static double worldToView(double p, double scale) {
        return p * scale;
    }

    /**
     * Translates view coordinate to a world coordinate.
     * Our assumption that zero world coordinate always
     * mapped to zero view coordinate.
     * @param p
     * @param scale
     * @return 
     */    
    public static double viewToWorld(double p, double scale) {
        return p / scale;
    }
    
    
}
