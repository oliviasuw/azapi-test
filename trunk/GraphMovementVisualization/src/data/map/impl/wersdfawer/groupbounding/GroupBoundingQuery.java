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

    private HashMap<String, HashMap<String, BoundingGroup>> groups;
    private HashMap<String, Collection<String>> subToGroup;
    private HashMap<String, HashMap<Class, Object>> metadata;

    public GroupBoundingQuery() {
        this.groups = new HashMap<>();
        this.subToGroup = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    @Override
    public void createGroup(String group, String subGroup, boolean movable) {
        HashMap<String, BoundingGroup> groupHash;
        if (!hasGroup(group)) {
            groupHash = new HashMap<>();
        }
        else {
            groupHash = groups.get(group);
        }
        groupHash.put(subGroup, new BoundingGroup(subGroup, movable));
        groups.put(group, groupHash);
        addSubToGroup(subGroup, group, movable);
    }

    private void addSubToGroup(String subgroup, String group, boolean movable) {
        Collection<String> grplist = subToGroup.get(subgroup);
        if (grplist == null) {
            grplist = new LinkedList<>();
            subToGroup.put(subgroup, grplist);
        }
        grplist.add(group);

        HashMap<String, BoundingGroup> subs = groups.get(group);
        subs.put(subgroup, new BoundingGroup(subgroup, movable));

    }

    @Override
    public Collection get(String group, String subGroup, double left, double right, double up, double down) {
        if (!hasGroup(group) || !hasSubGroup(group, subGroup)) {
            return null;
        }
        return groups.get(group).get(subGroup).get(left, right, up, down);
    }

    @Override
    public boolean addToGroup(String group, String subGroup, double x, double y, double width, double height, Object obj) {
        HashMap<String, BoundingGroup> subs = groups.get(group);
        if (subs.get(subGroup) == null) {
            return false;
        }
        return subs.get(subGroup).add(x, y, width, height, obj);
    }

    @Override
    public boolean hasGroup(String group) {
        return (groups.get(group) != null);
    }

    @Override
    public double[] getEpsilon(String group, String subGroup) {
        HashMap<String, BoundingGroup> get = groups.get(group);
        if (get == null || get.get(subGroup) == null) {
            return null;
        }
        return get.get(subGroup).getEpsilon();
    }

    @Override
    public Collection<String> getGroups(String subGroup) {
        return subToGroup.get(subGroup);
    }

    @Override
    public double[] getGroupEpsilon(String group) {
        HashMap<String, BoundingGroup> grpMap = groups.get(group);
        if (grpMap == null) {
            return null;
        }
        double[] ans = new double[2];
        for (String subgrp : grpMap.keySet()) {
            double epsW = grpMap.get(subgrp).getEpsilon()[0];
            double epsH = grpMap.get(subgrp).getEpsilon()[1];

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
     *
     * @return
     */
    public Set<String> getGroups() {
        return groups.keySet();
    }

    /**
     * returns all sub groups in bounding query.
     *
     * @return
     */
    public Set<String> getSubGroups() {
        return subToGroup.keySet();
    }

    public Set<String> getSubGroups(String group) {
        if (hasGroup(group)) {
            return groups.get(group).keySet();
        }
        return null;
    }

    public Object getMetaData(String group, Class classobj) {
        HashMap<Class, Object> get = metadata.get(group);
        return (get == null) ? null : get.get(classobj);
    }

    public void addMetaData(String group, Class classobj, Object Metadata) {
        HashMap<Class, Object> get = metadata.get(group);
        if (get == null) {
            get = new HashMap<>();
            metadata.put(group, get);
        }
        get.put(classobj, Metadata);
    }

    public boolean hasSubGroup(String group, String subGroup) {
        return groups.get(group).get(subGroup) != null;
    }
    
    public boolean isMoveable(String group, String subgroup) {
        return groups.get(group).get(subgroup).isMoveable();
    }
    
}
