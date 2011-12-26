/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.ano.Configuration;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.infra.AbstractTest;

/**
 *
 * @author bennyl
 */
@Register(name = "async-test", display = "Asynchronus Test")
public class AsyncTest extends AbstractTest {

    MessageDelayer dman = null;

    public MessageDelayer getMessageDelayer() {
        return dman;
    }

    @Configuration(name = "Message Delayer", description = "message delayer to add message delays")
    public void setMessageDelayer(MessageDelayer dman) {
        this.dman = dman;
    }

    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        if (dman != null) {
            AsyncExecution ret = new AsyncExecution(p, alg, this, new AsyncDelayedMailer(dman, p.getNumberOfVariables()), getExperiment());
            ret.setIdleDetectionNeeded(true);
            dman.initialize(ret);
            return ret;
        } else {
            return new AsyncExecution(p, alg, this, getExperiment());
        }
    }
    
}
