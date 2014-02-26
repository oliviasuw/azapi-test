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
public interface Action {

    void initialize(Player player);
    
    void update(double percentage);
    
    Action subAction(double percentageFrom, double percentageTo);
    
}
