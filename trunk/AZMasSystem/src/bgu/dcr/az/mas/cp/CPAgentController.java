/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.mas.AgentController;

/**
 *
 * @author User
 */
public interface CPAgentController extends AgentController {

    Problem getGlobalProblem();

    void report(String who, Agent a, Object[] data); //should be replaced with a better approach

    void assign(int id, int value);

    void unassign(int id);
    
    Integer getAssignment(int id);
    
    void assignAll(Assignment a);
}
