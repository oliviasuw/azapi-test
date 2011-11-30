/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.sync;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.infra.AbstractRound;

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
        return new SyncExecution(getPool(), p, alg, this);
    }
}
