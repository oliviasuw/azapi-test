/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

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
public class SimpleInfoStreamTest {

    public SimpleInfoStreamTest() {
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
        SimpleInfoStream infoStream = new SimpleInfoStream();

        Object rkey1 = new Object();
        Object rkey2 = new Object();

        int[] l1 = {0};
        int[] l2 = {0};

        assertTrue(infoStream.hasListeners(Info.class) == false);
        infoStream.listen(Info.class, rkey1, i -> l1[0]++);
        infoStream.listen(Info.class, rkey1, i -> l1[0]++);
        infoStream.listen(Info.class, rkey2, i -> l2[0]++);

        assertTrue(infoStream.hasListeners(Info.class) == true);
        infoStream.write(new Info());
        assertTrue(l1[0] == 2);
        assertTrue(l2[0] == 1);

        infoStream.removeListeners(rkey1);
        assertTrue(infoStream.hasListeners(Info.class) == true);
        infoStream.write(new Info());
        assertTrue(l1[0] == 2);
        assertTrue(l2[0] == 2);
    }

    private static final class Info {
    }

}
