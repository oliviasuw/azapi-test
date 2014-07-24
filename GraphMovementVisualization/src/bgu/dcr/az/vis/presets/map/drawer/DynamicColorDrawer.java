/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import data.map.impl.wersdfawer.GraphData;

/**
 *
 * @author Shlomi
 */
public class DynamicColorDrawer extends GroupDrawer {

    private final GraphData graphData;

    public DynamicColorDrawer(GraphData graphData, DrawerInterface drawer) {
        super(drawer);
        this.graphData = graphData;
    }
    
    @Override
    public void _draw(String group, String subgroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
