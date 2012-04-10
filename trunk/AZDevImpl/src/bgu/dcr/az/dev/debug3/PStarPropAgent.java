/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import java.util.HashMap;
import java.util.HashSet;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "PStarProp", useIdleDetector = false)
public class PStarPropAgent extends SimpleAgent {

    private HashSet<Integer> pStarDomain = null;
    private HashMap<Integer, HashSet<Integer>> pDomains = null;

    @Override
    public void start() {
        panicIf(pStarDomain == null, "Starting the run without a p* domain!");
        send("pStarDom", pStarDomain);
    }

    public void setPStarDomain(HashSet<Integer> ps) {
        pStarDomain = ps;
    }

    public HashMap<Integer, HashSet<Integer>> getpDomains() {
        return pDomains;
    }

    @WhenReceived("pStarDom")
    public void handlePStarDom(HashSet<Integer> ps) {
        pDomains.put(getCurrentMessage().getSender(), ps);
        if (pDomains.size() == getNeighbors().size()) {
            finish();
        }

    }
}