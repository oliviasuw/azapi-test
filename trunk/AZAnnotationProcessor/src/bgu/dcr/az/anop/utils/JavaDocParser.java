/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocParser {

    /**
     * return a map (annotation -> text) extracted from the given javadoc
     * string, the "" annotation is used to describe the default text.
     *
     * for example, for the javadoc:
     *
     * {@code
     * this is the default test
     *
     * @annotation1 hello world
     * @annotation2 other value }
     *
     * the resulted map will contain:{@code
     * "" -> "this is the default test",
     * "annotation1" -> "hello \nworld",
     * "annotation2" -> "other value" }
     *
     * @param javadoc
     * @return
     */
    public static Map<String, String> parse(String javadoc) {
        Map results = new HashMap();
        results.put("", javadoc);
        return results;
    }
}
