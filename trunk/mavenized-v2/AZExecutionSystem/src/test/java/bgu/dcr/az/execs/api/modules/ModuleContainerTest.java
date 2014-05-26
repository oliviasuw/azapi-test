/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.execs.api.modules;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bennyl
 */
public class ModuleContainerTest {
    
    public ModuleContainerTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of require method, of class ModuleContainer.
     */
    @org.junit.Test
    public void testConfiguration() throws IOException, ConfigurationException {
        Configuration mc = ConfigurationUtils.read(getClass().getResourceAsStream("ModuleContainerConfigurationFile.xml"));
        mc.properties().forEach(System.out::println);
        TestModuleContainer mcc =  mc.create();
        mcc.require(TestModule1.class);
        mcc.require(TestModule2.class);
    }
    
}
