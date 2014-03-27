/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.map.impl.wersdfawer;

/**
 *
 * @author Shl
 */
public interface GroupBoundingQueryInterface {
    
    public void createGroup(String group);
    
    //does all necessary calcs including epsilon and sorting
    public void get(String group, double left, double right, double up, double down);

    
}
