/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer.groupbounding;

import bgu.dcr.az.vis.player.api.Layer;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface GroupBoundingQueryInterface {

    /**
     * subgroup can be the layer of the group for example.
     * 
     * @param group
     * @param subgroup 
     * @param movable 
     */
    public void createGroup(String group, String subgroup, boolean movable);

    //does all necessary calcs including epsilon and sorting if need to
    public Collection get(String group, String subGroup, double left, double right, double up, double down);
    
    /**
     * returns a list of groups where this subgroup is.
     * @param subGroup
     * @return 
     */
    public Collection<String> getGroups(String subGroup);

    public boolean addToGroup(String group, String subGroup, double x, double y, double width, double height, Object obj);

    public boolean hasGroup(String group);

    public double[] getEpsilon(String group, String subGroup);
    
    public double[] getGroupEpsilon(String group);

}
