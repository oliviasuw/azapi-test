/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.map.impl.wersdfawer;

import data.map.impl.wersdfawer.groupbounding.HasId;
import java.util.HashMap;

/**
 *
 * @author Shl
 */
public class Edge implements HasId {
 
    public HashMap<String, String> tags;
    private final String id;

    public Edge(String id) {
        this.id = id;
        this.tags = new HashMap<>();
    }
    
    @Override
    public String getId() {
       return id;  
    }
    
    public String getTagValue(String tagKey) {
        return tags.get(tagKey);
    }
    
    public String addTag(String tagKey, String tagValue) {
        return tags.put(tagKey, tagValue);
    }
    
}
