/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.impl.infra.AbstractRound;

/**
 *
 * @author bennyl
 */
@Register(name="sync-round")
public class SyncRound extends AbstractRound {

    @Override
    protected void onConfigurationComplete() {
        //DONT CARE :)
    }


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new SyncExecution(getPool(), p, alg);
    }
}
