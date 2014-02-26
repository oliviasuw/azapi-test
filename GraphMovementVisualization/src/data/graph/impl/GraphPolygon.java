/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.graph.impl;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Shl
 */
public class GraphPolygon {
    private final Collection<String> nodes;
    private final HashMap<String, String> params;
    
    GraphPolygon(Collection<String> pNodes, HashMap<String, String> params) {
        this.nodes = pNodes;
        this.params = params;
    }

    public Collection<String> getNodes() {
        return nodes;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
    
    
    
}
