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
                "-f", "C:\\Users\\bennyl\\runtime-EclipseApplication\\NestedCheck\\test.xml",
                "-a", "ext.sim.agents.ABTAgentNew",
                "--cp", "C:\\Users\\bennyl\\runtime-EclipseApplication\\NestedCheck\\bin",
                "--gui"
        });
    }
}
