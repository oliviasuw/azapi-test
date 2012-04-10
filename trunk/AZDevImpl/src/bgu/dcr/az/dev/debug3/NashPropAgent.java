/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import java.util.HashSet;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;

@Algorithm(name = "NashProp", useIdleDetector = true)
public class NashPropAgent extends SimpleAgent {

    private NestableACNash ac;
    private NestablePStarProp propogator;

    @Override
    public void start() {
        calcAC(new NestableACNash(), new Continuation() {

            @Override
            public void doContinue() {
                System.out.println("DONE!~");
            }
        });
    }

    private void calcAC(final NestableACNash algo, final Continuation c) {
        algo.calculate(this).andWhenDoneDo(new Continuation() {

            @Override
            public void doContinue() {
                c.doContinue();
                calcProp(new NestablePStarProp(), algo.getReducedDomain(), c);
            }
        });
    }

    private void calcProp(final NestablePStarProp algo, HashSet<Integer> doms, final Continuation c) {
        algo.setPStarDomain(doms);
        algo.calculate(this).andWhenDoneDo(new Continuation() {

            @Override
            public void doContinue() {
                c.doContinue();
                return;
            }
        });
    }
}
