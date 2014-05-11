/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.map.impl.wersdfawer;

import data.map.impl.wersdfawer.groupbounding.HasId;

/**
 *
 * @author Shl
 */
public class Edge implements HasId{
 
    private String id;

    public Edge(String id) {
        this.id = id;
    }
    
    @Override
    public String getId() {
       return id;
       
    }
    
    
}
