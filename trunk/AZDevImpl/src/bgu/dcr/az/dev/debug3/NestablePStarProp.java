/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.Agt0DSL;
import java.util.HashMap;
import java.util.HashSet;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.NestableTool;

public class NestablePStarProp extends NestableTool {

    
    PStarPropAgent distPSPropogator;
    private HashSet<Integer> dom;
    
    @Override
    protected SimpleAgent createNestedAgent() {
        Agt0DSL.panicIf(dom == null, "PStar domain not set!");
        distPSPropogator = new PStarPropAgent();
        distPSPropogator.setPStarDomain(dom);
        return distPSPropogator;
    }

    public HashMap<Integer, HashSet<Integer>> getPDomains() {
        return distPSPropogator.getpDomains();
    }

    public void setPStarDomain(HashSet<Integer> dom) {
        this.dom = dom;
    }
}