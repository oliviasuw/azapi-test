/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import bgu.dcr.az.execs.exps.ModularExperiment;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author user
 */
public class AlgTest {

    public AlgTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() throws Exception{
        ModularExperiment.createDefault(getClass().getResourceAsStream("test.xml"), Executors.newCachedThreadPool());
    }
}
