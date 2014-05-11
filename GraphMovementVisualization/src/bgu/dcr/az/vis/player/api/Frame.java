/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.api;

import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;

/**
 *
 * @author Zovadi
 */
public interface Frame {
    
    Frame addAction(Action action);
    
    void initialize(Player player);
    
//    void update(double percentage);
     void update(double percentage, GroupBoundingQuery query);
     
}
