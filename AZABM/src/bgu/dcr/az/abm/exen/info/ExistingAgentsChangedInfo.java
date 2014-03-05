/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen.info;

/**
 *
 * @author bennyl
 */
public class ExistingAgentsChangedInfo {

    private int agentId;
    private ChangeType changeType;

    public ExistingAgentsChangedInfo(int agentId, ChangeType changeType) {
        this.agentId = agentId;
        this.changeType = changeType;
    }

    public int getAgentId() {
        return agentId;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

}
