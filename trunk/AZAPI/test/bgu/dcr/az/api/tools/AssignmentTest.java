/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class AssignmentTest {

    public AssignmentTest() {
    }

    @Test
    public void testEquals() {
        Assignment ass1 = new Assignment(1,1,2,2,3,3);
        Assignment ass2 = new Assignment(1,1,2,2);
        assertTrue("checking equals contract", ass1.equals(ass2) == ass2.equals(ass1));
    }

}
