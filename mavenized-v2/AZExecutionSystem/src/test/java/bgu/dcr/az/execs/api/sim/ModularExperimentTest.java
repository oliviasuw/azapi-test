/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.sim;

import bgu.dcr.az.execs.exps.ModularExperiment;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.factory.cglib.CglibProxyFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    public void testSanity() {
        ExecutorService pool = dummyExecutor();
        ModularExperiment exper;

        //1. test exception on no execution node provided
        try {
            exper = new ModularExperiment(pool);
            exper.execute();
            fail("no exception was thrown when attempting to execute modular experiment without supplying execution tree");
        } catch (Exception ex) {
        }

        //2. test no exception on execution node provided
        try {
            exper = new ModularExperiment(pool);
            
        } catch (Exception ex) {

        }

        //2. test getting a progress enhancer
    }

    @Test
    public void testCreateDefault_InputStream() throws Exception {
    }

    public ExecutorService dummyExecutor() {
        return (ExecutorService) new CglibProxyFactory().createInvokerProxy(new Invoker() {

            @Override
            public Object invoke(Object o, Method method, Object[] os) throws Throwable {
                return null;
            }
        }, new Class[]{ExecutorService.class});
    }
    
    

}
