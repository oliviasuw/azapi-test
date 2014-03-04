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
public class AgentDataChangedInfo {

    private int agentId;
    private Class dataClass;
    private ChangeType changeType;

    public AgentDataChangedInfo(int agentId, Class dataClass, ChangeType changeType) {
        this.agentId = agentId;
        this.dataClass = dataClass;
        this.changeType = changeType;
    }

    public int getAgentId() {
        return agentId;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

}
