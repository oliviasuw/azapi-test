/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.async;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.impl.infra.AbstractRound;

/**
 *
 * @author bennyl
 */
@Register(name= "async-round")
public class AsyncRound extends AbstractRound {

    @Override
    protected void onConfigurationComplete() {
        //DONT CARE :)
    }


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new AsyncExecution(getPool(), p, alg);
    }
}
