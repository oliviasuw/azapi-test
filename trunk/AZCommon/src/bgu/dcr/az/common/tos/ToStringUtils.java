/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.tos;

import java.util.Arrays;

/**
 *
 * @author User
 */
public class ToStringUtils {

    public static String toString(Object o) {
        StringBuilder sb = new StringBuilder();
        if (o == null) {
            sb.append("null");
        } else if (o.getClass().isArray()) {
            final Class<?> ctype = o.getClass().getComponentType();

            switch (ctype.getSimpleName()) {
                case "int":
                    sb.append(Arrays.toString((int[]) o));
                    break;
                case "long":
                    sb.append(Arrays.toString((long[]) o));
                    break;
                case "short":
                    sb.append(Arrays.toString((short[]) o));
                    break;
                case "double":
                    sb.append(Arrays.toString((double[]) o));
                    break;
                case "float":
                    sb.append(Arrays.toString((float[]) o));
                    break;
                case "byte":
                    sb.append(Arrays.toString((byte[]) o));
                    break;
                case "char":
                    sb.append(Arrays.toString((char[]) o));
                    break;
                case "boolean":
                    sb.append(Arrays.toString((boolean[]) o));
                    break;
                default:
                    sb.append(Arrays.toString((Object[]) o));
                    break;
            }
        } else {
            sb.append(o.toString());
        }

        return sb.toString();
    }
}
