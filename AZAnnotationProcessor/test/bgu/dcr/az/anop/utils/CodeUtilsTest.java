/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class CodeUtilsTest {

    public CodeUtilsTest() {
    }

    @Test
    public void testCamelCaseToLowerLine() {
        String testResult = CodeUtils.camelCaseToLowerLine("thisIsAnIdentifierName");
        assertTrue("got: " + testResult, testResult.equals("this_is_an_identifier_name"));
    }

}
