/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution.statistics;

import bgu.dcr.az.execs.statistics.info.MessageInfo;

/**
 *
 * @author Zovadi
 */
public class CPMessageInfo extends MessageInfo {

    private final long ccs;
    private final boolean isInternal;

    public CPMessageInfo(long messageId, int sender, int recepient, String name, long ccs, OperationType operation, boolean isInternal) {
        super(messageId, sender, recepient, name, operation);
        this.ccs = ccs;
        this.isInternal = isInternal;
    }

    public long getConstraintChecks() {
        return ccs;
    }

    public boolean isInternal() {
        return isInternal;
    }

}
