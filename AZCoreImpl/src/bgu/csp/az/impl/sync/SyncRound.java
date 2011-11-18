/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.impl.infra.AbstractRound;

/**
 *
 * @author bennyl
 */
public class SyncRound extends AbstractRound {

    @Override
    public String getConfigurationName() {
        return "sync-round";
    }

    @Override
    public String getConfigurationDescription() {
        return "configurable part of an expirement";
    }

    @Override
    protected void onConfigurationComplete() {
        //DONT CARE :)
    }


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new SyncExecution(getPool(), p, alg);
    }
}
