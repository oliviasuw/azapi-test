/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.api;

/**
 *
 * @author Zovadi
 */
public interface Command {
    
    void update(double percentage);
    
    void initialize(Player player);

    //getEntityId shouldnt be in the interface but its an ok function for certain ones
}
