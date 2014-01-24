/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.agt;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.mas.DCRAgentController;

/**
 *
 * @author Inna
 */
public class ReportMediator {

    Object[] args;
    Agent a;
    DCRAgentController controller;

    public ReportMediator(Object[] args, Agent a, DCRAgentController controller) {
        this.args = args;
        this.a = a;
    }

    /**
     * send report to the given module name
     *
     * @param who
     */
    public void to(String who) {
        controller.report(who, a, args);
    }
}
