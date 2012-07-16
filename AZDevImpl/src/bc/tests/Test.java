/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.tests;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.exen.cli.AZEXE;
import bgu.dcr.az.exen.cli.AmZRun;
import java.io.IOException;

/**
 *
 * @author bennyl
 */
public class Test {

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        AmZRun.main(Agt0DSL.array("-e", "exp.xml", "-rdir", "results", "-tn", "AC-ANT", "-an", "__SBB", "-pn", "7"));
    }
}