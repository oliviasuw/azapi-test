package bgu.dcr.autogen.registry;

import bgu.dcr.az.anop.reg.impl.RegisteryImpl;
import bgu.dcr.az.anop.reg.impl.Registration;

public class bgu_dcr_az_algos_SBBAgent implements Registration {

    @Override
    public void register(RegisteryImpl registery) {
        registery.register(bgu.dcr.az.algos.SBBAgent.class, "ALGORITHM.SBB");
    }
    
}
