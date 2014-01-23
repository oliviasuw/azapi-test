/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp.executions;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.execs.AbstractProc;

/**
 *
 * @author User
 */
public class SingleAgentRunner extends AbstractProc {

    private Agent a;

    public SingleAgentRunner(Agent a) {
        super(a.getId());
        this.a = a;
    }

    @Override
    protected void start() {
        System.out.println("Agent Runner for: " + pid() + " started");
        terminate();
    }

    @Override
    protected void quota() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
