/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer.groupbounding;

import com.bbn.openmap.util.quadtree.QuadTree;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Shl
 */
public class GroupBoundingQuery implements GroupBoundingQueryInterface {

    private HashMap<String, BoundingGroup> groups;
    private HashMap<String, Collection<String>> subToGroup;

    public GroupBoundingQuery() {
        this.groups = new HashMap<>();
        this.subToGroup = new HashMap<>();
    }

    @Override
    public void createGroup(String group, String subGroup, GroupMetaData metadata, boolean movable) {
        addSubToGroup(subGroup, group);

        groups.put(group, new BoundingGroup(subGroup, metadata, movable));
    }


    private void addSubToGroup(String subgroup, String group) {
        Collection<String> grplist = subToGroup.get(subgroup);
        if (grplist == null) {
            grplist = new LinkedList<>();
            subToGroup.put(subgroup, grplist);
        }
        grplist.add(group);
    }

    @Override
    public Collection get(String group, double left, double right, double up, double down) {
        if (!hasGroup(group)) {
            return null;
        }
        return groups.get(group).get(left, right, up, down);
    }

    @Override
    public boolean addToGroup(String group, double x, double y, double width, double height, Object obj) {
        BoundingGroup bgrp = groups.get(group);
        if (bgrp == null) {
            return false;
        }
        return bgrp.add(x, y, width, height, obj);
    }

    @Override
    public boolean hasGroup(String group) {
        return (groups.get(group) != null);
    }

    @Override
    public double[] getEpsilon(String group) {
        BoundingGroup get = groups.get(group);
        if (get == null) {
            return null;
        }
        return get.getEpsilon();
    }

    @Override
    public Collection<String> getGroups(String subGroup) {
        return subToGroup.get(subGroup);
    }

    @Override
    public double[] getSubGroupEpsilon(String subGroup) {
        Collection<String> grplist = subToGroup.get(subGroup);
        if (grplist == null) {
            return null;
        }
        double[] ans = new double[2];
        for (String grp : grplist) {
            double epsW = groups.get(grp).getEpsilon()[0];
            double epsH = groups.get(grp).getEpsilon()[1];

            if (ans[0] < epsW) {
                ans[0] = epsW;
            }
            if (ans[0] < epsH) {
                ans[1] = epsH;
            }
        }
        return ans;
    }

    /**
     * returns all groups in bounding query.
     * @return 
     */    
    public Set<String> getGroups() {
        return groups.keySet();
    }
    
    /**
     * returns all sub groups in bounding query.
     * @return 
     */    
    public Set<String> getSubGroups() {
        return subToGroup.keySet();
    }
    
    public String getSubGroup(String group) {
        if (hasGroup(group)) {
            return groups.get(group).getSubGroup();
        }
        return null;
    }

}
