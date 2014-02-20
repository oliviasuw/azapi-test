/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import graphmovementvisualization.Location;

/**
 *
 * @author Shl
 */
public interface Drawable {

    public void draw();

    public Location getLocation();

    public void setLocation();

    public boolean isVisible();

    public void setVisible(boolean visible);

}
