/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.impl.JavaDocInfoImpl;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocParser {

    public static JavaDocInfo parse(String javadoc) {
        Map results = new HashMap();
        results.put("", javadoc);
        return new JavaDocInfoImpl(results);
    }
}
