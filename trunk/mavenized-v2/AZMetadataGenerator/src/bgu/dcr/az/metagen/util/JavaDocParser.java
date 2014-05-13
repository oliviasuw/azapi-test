/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.metagen.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocParser {

    private static String compileJavaDoc(String javadoc) {
        javadoc = javadoc.trim();
        String[] lines = javadoc.split("\n");
        StringBuilder sb = new StringBuilder();
        
        for (String l : lines) {
            sb.append(l.trim()).append("\n");
        }

        return sb.toString();
    }

    public static JavaDocInfo parse(String javadoc) {
    	String original = javadoc;
        Map<String, List<String>> results = new HashMap();

        if (javadoc == null) javadoc = "";
        
        javadoc = compileJavaDoc(javadoc);

        javadoc = "@ " + javadoc;

        for (int nextTag = nextTagIndex(javadoc); nextTag != javadoc.length();) {
            int temp = nextTagIndex(javadoc, nextTag + 1);
            temp = temp == -1 ? javadoc.length() : temp;
            retrieveData(javadoc.substring(nextTag + 1, temp), results);
            nextTag = temp;
        }

        return new JavaDocInfo(results, original);
    }

    private static void retrieveData(String javadoc, Map<String, List<String>> results) {
        int tagEnd = javadoc.indexOf(' ');
        if (tagEnd == -1) {
            return;
        }
        String tag = javadoc.substring(0, tagEnd).trim();
        String data = javadoc.substring(tagEnd).trim();

        if (!results.containsKey(tag)) {
            results.put(tag, new LinkedList<String>());
        }
        results.get(tag).add(data);
    }

    private static int nextTagIndex(String javadoc) {
        return nextTagIndex(javadoc, 0);
    }

    private static int nextTagIndex(String javadoc, int startIndex) {
        for (int i = startIndex, p = 0; i < javadoc.length(); i++) {
            switch (javadoc.charAt(i)) {
                case '{':
                    p++;
                    break;
                case '}':
                    p--;
                    break;
                case '@':
                    if (p == 0) {
                        return i;
                    }
            }
        }
        return -1;
    }
}
