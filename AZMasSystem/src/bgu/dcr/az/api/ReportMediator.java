/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.mas.cp.CPAgentController;

/**
 *
 * @author Inna
 */
public class ReportMediator {

    Object[] args;
    Agent a;
    CPAgentController controller;

    public ReportMediator(Object[] args, Agent a, CPAgentController controller) {
        this.args = args;
        this.a = a;
    }

    /**
     * send report to the given module name
     *
     * @param who
     */
    public void to(String who) {
        //controller.report(who, a, args);
    }
}
