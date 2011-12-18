/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.infra.AbstractRound;

/**
 *
 * @author bennyl
 */
@Register(name= "async-round", display="Asynchronus Round")
public class AsyncRound extends AbstractRound {

    @Override
    protected void onConfigurationComplete() {
        //DONT CARE :)
    }


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new AsyncExecution(getPool(), p, alg, this);
    }

}
