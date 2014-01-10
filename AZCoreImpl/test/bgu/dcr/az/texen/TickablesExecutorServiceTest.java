/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.texen;

import java.lang.reflect.Constructor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class TickablesExecutorServiceTest {

    private TickablesExecutorService service;

    public TickablesExecutorServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("Starting Test... ");
    }

    @After
    public void tearDown() {
        service.shutdown();
    }

    /**
     * Test of execute method, of class TickablesExecutorService.
     */
//    @Ignore
    @Test(timeout = 1000)
    public void testSanityNotParallel() throws Exception {
        int numberOfTickables = 100;
        int numberOfThreads = 1;

        TestResult result = executeSimpleTest(SanityTickable.class, numberOfTickables, numberOfThreads, null);
        for (int t : result.numberOfTicksPerTickable) {
            assertTrue("Expecting 2 ticks per agent - got " + t, t == 2);
        }
//        assertTrue("Expecting 2*" + );
        assertTrue("Expecting GRACEFUL TERMINATION GOT: " + result.executionResult, result.executionResult == TickablesExecutorService.TerminationStatus.GRACEFULLY);
    }

//    @Ignore
    @Test(timeout = 1000)
    public void testSanityMoreThreadsNotParallel() throws Exception {
        TestResult result = executeSimpleTest(SanityTickable.class, 100, 10, null);
        for (int t : result.numberOfTicksPerTickable) {
            assertTrue("Expecting 2 ticks per agent - got " + t, t == 2);
        }
        assertTrue(result.executionResult == TickablesExecutorService.TerminationStatus.GRACEFULLY);
    }

//    @Ignore
    @Test(timeout = 1000)
    public void testCrushingMoreThreadsNotParallel() throws Exception {
        TestResult result = executeSimpleTest(CrushingTickable.class, 100, 10, null);
        assertTrue(result.executionResult == TickablesExecutorService.TerminationStatus.CRUSHED);
    }

    @Test(timeout = 1000)
    public void testIdleSingleThread() throws Exception {

        TestResult result = executeSimpleTest(IdleTickable.class, 100, 1, new IdleDetectionCallback() {

            @Override
            public void onIdleDetection() {
                service.interupt();
            }
        });
        assertTrue(result.executionResult == TickablesExecutorService.TerminationStatus.INTERUPTED);
        assertTrue("Expecting IdleDetections: 1 got " + result.numberOfIdleDetections, result.numberOfIdleDetections == 1);
    }

    @Test(timeout = 1000)
    public void simpleIdleDetectionSingleThreadTest() throws Exception {
        TestResult result = executeSimpleTest(SimpleIdleTickable.class, 1, 1, new IdleDetectionCallback() {

            @Override
            public void onIdleDetection() {
                service.interupt();
            }
        });
        assertTrue("Expecting Interuption Exit Status, got: " + result.executionResult, result.executionResult == TickablesExecutorService.TerminationStatus.INTERUPTED);
        assertTrue("Expecting IdleDetections: 1 got " + result.numberOfIdleDetections, result.numberOfIdleDetections == 1);
    }

    private TestResult executeSimpleTest(Class<? extends Tickable> tickableClass, int numberOfTickables, int numberOfThreads, final IdleDetectionCallback callback) throws Exception {
        service = new TickablesExecutorService(numberOfThreads);
        Tickable[] tickables = new Tickable[numberOfTickables];

        final TestResult result = new TestResult(numberOfTickables);
        Constructor<? extends Tickable> constructor = tickableClass.getConstructor(int.class, TickablesExecutorService.class, int.class, TestResult.class
        );
        for (int i = 0;
                i < tickables.length;
                i++) {
            tickables[i] = constructor.newInstance(i, service, tickables.length, result);
        }

        service.addIdleDetectionCallback(
                new IdleDetectionCallback() {

                    @Override
                    public void onIdleDetection() {
                        result.numberOfIdleDetections++;
                        if (callback != null) {
                            callback.onIdleDetection();
                        }
                    }
                }
        );
        result.executionResult = service.execute(tickables);
        return result;
    }

}
