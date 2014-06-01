/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author bennyl
 */
public class ModularExperimentTest {

    public ModularExperimentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testLoadingSimple() throws IOException, ConfigurationException {
        Configuration conf = ConfigurationUtils.read(getClass().getResourceAsStream("test-simple.xml"));
        ExecutionTree etree = conf.create();
        
        assertTrue(etree.getName().equals("test"));
        assertTrue(etree.isInstalled(RegisteredModule1.class));
        
    }
}
