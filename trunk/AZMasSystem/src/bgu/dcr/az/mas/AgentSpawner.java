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
public interface AgentSpawner extends ExecutionService{

    Class getAgentType(int id);
}
