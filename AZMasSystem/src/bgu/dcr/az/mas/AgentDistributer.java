/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

/**
 *
 * @author User
 */
public interface AgentDistributer extends ExecutionService {

    int[] getControlledAgentsIds(int controllerId);

    int getNumberOfAgents();

    int getNumberOfAgentControllers();
}
