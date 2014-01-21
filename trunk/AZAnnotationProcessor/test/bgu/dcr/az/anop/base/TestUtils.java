/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;

/**
 *
 * @author User
 */
public class TestUtils {

    public static void assertSameElements(String message, Collection c1, Collection c2) {
        List cc2 = new LinkedList(c2);

        for (Object c : c1) {
            if (!cc2.remove(c)) {
                Assert.fail(message);
            }
        }

        if (!cc2.isEmpty()) {
            Assert.fail(message);
        }
    }

    public static void assertSameElements(String message, Collection c1, Object... elements) {
        assertSameElements(message, c1, Arrays.asList(elements));
    }
}
