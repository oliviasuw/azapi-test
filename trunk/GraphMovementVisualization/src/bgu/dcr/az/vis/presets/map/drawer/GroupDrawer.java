/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.presets.map.drawer;

/**
 *
 * @author Shl
 */
public abstract class GroupDrawer implements GroupDrawerInterface {
    protected DrawerInterface drawer;

    public GroupDrawer(DrawerInterface drawer) {
        this.drawer = drawer;
    }
    
}
