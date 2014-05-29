/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim;

/**
 *
 * @author User
 */
public class AgentContext {

    private final String contextRepresentation;
    private final long contextId;

    AgentContext(String contextRepresentation, long contextId) {
        this.contextRepresentation = contextRepresentation;
        this.contextId = contextId;
    }

    public long getContextId() {
        return contextId;
    }

    public String getContextRepresentation() {
        return contextRepresentation;
    }

}
