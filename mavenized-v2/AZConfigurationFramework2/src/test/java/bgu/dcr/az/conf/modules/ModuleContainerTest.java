/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

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
        TestModuleContainer mcc = mc.create();
        mcc.require(TestModule1.class);
        mcc.require(TestModule2.class);
    }

    @Test
    public void testRequireAll() {
        ModuleContainer mc = new ModuleContainer();
        TestModule1 tm1 = new TestModule1();
        TestModule1 tm12 = new TestModule1();

        mc.installAll(TestModule1.class, tm1, tm12);
        Iterable<TestModule1> iall = mc.requireAll(TestModule1.class);
        Set<TestModule1> lall = IterableUtils.toSet(iall);

        assertTrue("two modules was loaded", lall.size() == 2);
        assertTrue("what we put we get", lall.containsAll(Arrays.asList(tm1, tm12)));
    }

    @Test
    public void testRequireAllIterator() {
        ModuleContainer root = new ModuleContainer();
        ModuleContainer mc = new ModuleContainer();
        root.install(mc);
        TestModule1 tm1 = new TestModuleExtending1();
        TestModule2 tm2 = new TestModule2();
        mc.install(tm1);
        mc.install(tm2);
        
        root.initializeModules();

        int sum = 0;
        for (TestModule1 module : mc.requireAll(TestModule1.class)) {
            sum++;
            if (module != tm1) {
                fail("iterator return unexpected item");
            }
        }
        
        if (sum > 1) {
            fail("iterator return too much elements: " + sum);
        }
    }

    @Test
    public void testNoReplicationOfModulesToParent() {
        ModuleContainer root = new ModuleContainer();
        ModuleContainer child = new ModuleContainer();

        root.install(ModuleContainer.class, child);
        child.install(TestModule1.class, new TestModule1());

        assertTrue(IterableUtils.toList(root.requireAll(ModuleContainer.class)).size() == 1);
        assertTrue(IterableUtils.toList(child.requireAll(ModuleContainer.class)).isEmpty());
        assertTrue(0 == IterableUtils.toList(root.requireAll(TestModule1.class)).size());
        assertTrue(IterableUtils.toList(child.requireAll(TestModule1.class)).size() == 1);
    }

    @Test
    public void testLazyInitialization() {
        ModuleContainer root = new ModuleContainer();
        TestModule1 tm1 = new TestModule1();
        root.install(tm1);

        assertTrue(!tm1.isInitialized());
        root.initializeModules();
        assertTrue(tm1.isInitialized());
    }

    @Test
    public void testEagerInitialization() {
        ModuleContainer root = new ModuleContainer(true);
        TestModule1 tm1 = new TestModule1();
        root.install(tm1);

        assertTrue(tm1.isInitialized());
    }
    
    @Test 
    public void testUnistallation(){
        ModuleContainer root = new ModuleContainer();
        ModuleContainer child1 = new ModuleContainer(true);
        TestModule1 tm1 = new TestModule1();
        TestModule1 tm1Root = new TestModule1();
        TestModuleExtending1 tm11 = new TestModuleExtending1();
        TestModule2 tm2 = new TestModule2();
        
        root.install(child1);
        root.install(tm1Root);
        child1.install(tm1);
        child1.install(tm11);
        child1.install(tm2);
        
        child1.uninstallLocally(TestModule1.class);
        assertTrue(child1.isInstalled(tm11, TestModuleExtending1.class));
        assertTrue(!child1.isInstalled(tm1, TestModule1.class));
        assertTrue(!child1.isInstalled(tm1, TestModuleExtending1.class));
        assertTrue(root.isInstalled(tm1Root, TestModule1.class));
        
        child1.uninstallLocally(tm2);
        assertTrue(!child1.isInstalled(tm2, TestModule2.class));
        
        
    }

}
