/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.sync;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.infra.AbstractTest;

/**
 *
 * @author bennyl
 */
@Register(name="sync-test", display="Synchronus Test")
public class SyncTest extends AbstractTest {


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new SyncExecution(getPool(), p, alg, this);
    }
}
