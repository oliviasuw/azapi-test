/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

/**
 *
 * @author bennyl
 */
public class TestMain {
    public static void main(String[] args){
        Agent0Tester.main(new String[]{
                "-f", "C:\\Users\\Inna\\workspace\\DPOP\\test.xml",
                "-a", "ext.sim.agents.DPOPAgent",
                "--cp", "C:\\Users\\Inna\\workspace\\DPOP\\bin",
                "--gui"
        });
    }
}
