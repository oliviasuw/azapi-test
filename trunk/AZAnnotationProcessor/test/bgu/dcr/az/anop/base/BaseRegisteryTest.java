/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.base;

import bgu.dcr.az.anop.reg.impl.RegisteryImpl;
import bgu.dcr.az.anop.reg.Register;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author User
 */
public class BaseRegisteryTest {

    private RegisteryImpl registery;

    public BaseRegisteryTest() {
    }

    @Before
    public void initialize() {
        registery = new RegisteryImpl();
        registery.register(B.class, "CLASS B");
        registery.register(D.class, "CLASS D");
        registery.register(E.class, "CLASS E");
        registery.register(F.class, "CLASS F");

    }

    @Test
    public void testNameToClassToName() {
        assertTrue(registery.getRegisteredClassName(B.class).equals("CLASS B"));
        assertTrue(registery.getRegisteredClassName(E.class).equals("CLASS E"));
        assertTrue(registery.getRegisteredClassName(AA.class) == null);
        
        assertTrue(registery.getRegisteredClassByName("CLASS B").equals(B.class));
        assertTrue(registery.getRegisteredClassByName("CLASS E").equals(E.class));
        assertTrue(registery.getRegisteredClassByName("BLA BLA") == null);
    }

    @Test
    public void testImplementors() {

        Collection<Class> implementors = registery.getImplementors(A.class);
        TestUtils.assertSameElements("implementors of A (" + implementors + ")", implementors, B.class);

        implementors = registery.getImplementors(IA.class);
        TestUtils.assertSameElements("implementors of IA (" + implementors + ")", implementors, D.class, F.class, E.class);

        implementors = registery.getImplementors(E.class);
        TestUtils.assertSameElements("implementors of E (" + implementors + ")", implementors, E.class);

        implementors = registery.getImplementors(AA.class);
        TestUtils.assertSameElements("implementors of AA (" + implementors + ")", implementors, E.class, F.class);

    }

    public static class A {

    }

    @Register("CLASS B")
    public static class B extends A {

    }

    public static interface IA {

    }

    public static interface IB extends IA {

    }

    public static class C implements IA, IB {

    }

    @Register("CLASS D")
    public static class D extends C {

    }

    public static interface ID {

    }

    public static abstract class AA extends D implements ID {

    }

    public static abstract class AB extends AA {

    }

    @Register("CLASS E")
    public static class E extends AA {

    }

    @Register("CLASS F")
    public static class F extends AB {

    }

}
