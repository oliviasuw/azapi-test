/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer.groupbounding;

import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import com.bbn.openmap.util.quadtree.QuadTree;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Shl
 */
public class GroupBoundingQuery implements GroupBoundingQueryInterface {

    private HashMap<String, HashMap<String, BoundingGroup>> groups;
    private HashMap<String, Collection<String>> subToGroup;
    private HashMap<String, HashMap<Class, Object>> metadata;
    private final Set<String> movable;
    private HashMap<String, HasId> idToEntity;
    private HashMap<String, String[]> idToGroup;

    public GroupBoundingQuery() {
        this.groups = new HashMap<>();
        this.subToGroup = new HashMap<>();
        this.metadata = new HashMap<>();
        this.movable = new HashSet<>();

        this.idToEntity = new HashMap<>();
        this.idToGroup = new HashMap<>();

    }

    @Override
    public void createGroup(String group, String subGroup, boolean movable) {
        HashMap<String, BoundingGroup> groupHash;
        if (!hasGroup(group)) {
            groupHash = new HashMap<>();
        } else {
            groupHash = groups.get(group);
        }
        groupHash.put(subGroup, new BoundingGroup(subGroup, movable));
        groups.put(group, groupHash);
        addSubToGroup(subGroup, group, movable);
        if (movable) {
            this.movable.add(group);
        }
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
    
    /**
     * get from any subgroup in the specified group.
     * @param group
     * @param left
     * @param right
     * @param up
     * @param down
     * @return 
     */
    public Collection get(String group, double left, double right, double up, double down) {
        if (!hasGroup(group)) {
            return null;
        }
        Set<String> subs = getSubGroups(group);
        Vector retVec = new Vector();
        for (String subGroup : subs) {
            Vector get = groups.get(group).get(subGroup).get(left, right, up, down);
            retVec.addAll(get);
        }
        return retVec;
    }

    @Override
    public boolean addToGroup(String group, String subGroup, double x, double y, double width, double height, HasId obj) {
        HashMap<String, BoundingGroup> subs = groups.get(group);
        if (subs.get(subGroup) == null) {
            return false;
        }
        idToEntity.put(obj.getId(), obj);
        idToGroup.put(obj.getId(), new String[]{group, subGroup});
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
        ans[0]++;
        ans[1]++;
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

    /**
     * a group is moveable if at least one of its subgroups is moveable.
     * although possible, it is not adviced to mix unmoveable and movebale sub
     * groups in the same group.
     *
     * @param group
     * @param subgroup
     * @return
     */
    public boolean isMoveable(String group) {
        return movable.contains(group);
    }

    @Override
    public Object remove(String group, String subgroup, double x, double y, HasId obj) {
        idToEntity.remove(obj.getId());
        idToGroup.remove(obj.getId());
        return groups.get(group).get(subgroup).remove(x, y, obj);
    }

    public Object getById(String id) {
        return idToEntity.get(id);
    }

    public String[] getGroupDetails(String entityId) {
        return idToGroup.get(entityId);
    }

    public void remove(HasId entity, double x, double y) {
        String[] groupDetails = idToGroup.remove(entity.getId());
        if (groupDetails != null) {
            remove(groupDetails[0], groupDetails[1], x, y, entity);
        }
    }

}
