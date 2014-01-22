/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.impl.JavaDocInfoImpl;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public class JavaDocParser {

    public static void main(String[] args) {
        String javadoc = "   /**\n"
                + "     * Allocates a new {@code String} that contains characters from a subarray\n"
                + "     * of the <a href=\"Character.html#unicode\">Unicode code point</a> array\n"
                + "     * argument.  The {@code offset} argument is the index of the first code\n"
                + "     * point of the subarray and the {@code count} argument specifies the\n"
                + "     * length of the subarray.  The contents of the subarray are converted to\n"
                + "     * {@code char}s; subsequent modification of the {@code int} array does not\n"
                + "     * affect the newly created string.\n"
                + "     *\n"
                + "     * @param  codePoints\n"
                + "     *         Array that is the source of Unicode code points\n"
                + "     *\n"
                + "     * @param  offset\n"
                + "     *         The initial offset\n"
                + "     *\n"
                + "     * @param  count\n"
                + "     *         The length\n"
                + "     *\n"
                + "     * @throws  IllegalArgumentException\n"
                + "     *          If any invalid Unicode code point is found in {@code\n"
                + "     *          codePoints}\n"
                + "     *\n"
                + "     * @throws  IndexOutOfBoundsException\n"
                + "     *          If the {@code offset} and {@code count} arguments index\n"
                + "     *          characters outside the bounds of the {@code codePoints} array\n"
                + "     *\n"
                + "     * @since  1.5\n"
                + "     */\n";
        JavaDocInfo jd = parse(javadoc);
        
        System.out.println(jd.description());
        System.out.println("---------------------------------------------");
        
        for (String t : jd.tags()) {
            System.out.println(t + ":\n" + jd.tag(t));
            System.out.println("---------------------------------------------");
        }
    }

    private static String compileComments(String javadoc) {
        javadoc = javadoc.trim();
        int startIndex = javadoc.indexOf("/*");
        if (startIndex == -1) {
            return null;
        }
        javadoc = javadoc.substring(startIndex + "/*".length());
        javadoc = new StringBuilder().append(javadoc).reverse().toString();

        int endIndex = javadoc.indexOf("/*");
        if (startIndex == -1) {
            return null;
        }
        javadoc = javadoc.substring(endIndex + "/*".length());
        javadoc = new StringBuilder().append(javadoc).reverse().toString();
        String[] lines = javadoc.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String l : lines) {
            int asterixIndex = l.indexOf('*');
            if (asterixIndex != -1) {
                sb.append(l.substring(asterixIndex + 1).trim()).append("\n");
            }
        }

        return sb.toString();
    }

    public static JavaDocInfo parse(String javadoc) {
        Map<String, List<String>> results = new HashMap();

        javadoc = compileComments(javadoc);

        javadoc = "@ " + javadoc;

        for (int nextTag = nextTagIndex(javadoc); nextTag != javadoc.length();) {
            int temp = nextTagIndex(javadoc, nextTag + 1);
            temp = temp == -1 ? javadoc.length() : temp;
            retrieveData(javadoc.substring(nextTag + 1, temp), results);
            nextTag = temp;
        }

        return new JavaDocInfoImpl(results);
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
