/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

/**
 *
 * @author User
 */
public class StringUtils {

    /**
     * return a string containing all the escaped symboles for example for the
     * string null     {@code 
     * line1
     *      tabbed line2
     * }
     * you will get the string {@code line1\n\ttabbed line2}
     *
     * @param s
     * @return
     */
    public static String escapedString(String s) {
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();

        for (char c : chars) {
            switch (c) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }
}
