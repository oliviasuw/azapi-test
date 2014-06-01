/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author bennyl
 */
public class ControllerTest {

    public ControllerTest() {
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
    public void testManageControllers() {
        RootController root = new RootController();
        Controller child = spy(new TestDummyController());

        //check the lifecycle of load view
        verify(child, times(0)).onLoadView();
        root.install(child);

        verify(child, times(0)).onLoadView();
        verify(child, times(1)).installInto(root);

        assertTrue(root.isInstalled(child));

        root.loadView();
        verify(child, times(1)).loadView();
    }

    @Test
    public void testFindAndManageControllers() {
        RootController root = new RootController();
        Controller dummy = root.findAndInstall("test.dummy");

        checkControllerManagement(root, dummy, false);

    }

    private void checkControllerManagement(RootController root, Controller dummy, boolean isLoaded) {
        assertTrue(root.isInstalled(dummy));
        assertTrue("dummy type: " + dummy.getClass().getSimpleName(), dummy instanceof TestDummyController);

        TestDummyController tdummy = (TestDummyController) dummy;

        if (!isLoaded) {
            assertTrue("calls to tdummy.getOnLoadViewCalls(): " + tdummy.getOnLoadViewCalls(), tdummy.getOnLoadViewCalls() == 0);
            root.loadView();
        }

        assertTrue(tdummy.getOnLoadViewCalls() == 1);
    }

    @Test
    public void testFindAndManageAllControllers() {
        RootController root = new RootController();
        Iterable<Controller> dummy = root.findAndInstallAll("test");

        root.loadView();
        dummy.forEach(d -> {
            checkControllerManagement(root, d, true);
        });
    }
    
    @Test
    public void testListenersRemovedWhenControllerRemoved(){
        RootController root = new RootController();
        TestDummyController dummy = root.findAndInstall("test.dummy");
        
        int[] calls = {0};
        dummy.infoStream().listen(Info.class, i -> calls[0]++);
        
        root.infoStream().write(new Info());
        assertTrue(calls[0] == 1);
        dummy.uninstall();
        root.infoStream().write(new Info());
        assertTrue(calls[0] == 1);
    }
    
    private static final class Info{
        
    }

}
