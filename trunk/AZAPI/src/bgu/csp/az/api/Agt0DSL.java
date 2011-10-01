/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * Simulator Domain Specific Language Object is a group of functions that simplify some programming tasks
 * designed for the lazy programmer :)
 * 
 * under constraction!
 * @author bennyl
 */
public class Agt0DSL {

    /**
     * returns a collection of numbers in the range of start to end (includes start and end)
     * @param start
     * @param end
     * @return
     */
    public static List<Integer> range(int start, int end) {
        if (end < start) {
            return new ArrayList<Integer>(0);
        }

        ArrayList<Integer> ret = new ArrayList<Integer>(end - start);
        for (int i = start; i <= end; i++) {
            ret.add(i);
        }
        return ret;
    }

    /**
     * perform equals on obj1 and obj2 but take null into consideration
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean eq(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    /**
     * perform non binary equales
     * @param all
     * @return
     */
    public static boolean eq(Object... all) {
        if (all.length == 0) {
            return true;
        }

        for (int i = 1; i < all.length; i++) {
            if (!eq(all[0], all[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param who
     * @param ors
     * @return true if @param who is one of the @param ors
     */
    public static boolean isOneOf(Object who, Object... ors) {
        for (Object o : ors) {
            if (eq(who, o)) {
                return true;
            }
        }
        return false;
    }

    public static String str(Collection col) {
        if (col == null) {
            return "![]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object c : col) {
            sb.append(c.toString()).append(", ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");

        return sb.toString();
    }

    public static String drop(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(sum);
    }

    public static String take(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(0, sum);
    }

    public static boolean isNummeric(char who) {
        return who >= '0' && who <= '9';
    }

    public static boolean isNummeric(String who) {
        if (who.isEmpty()) {
            return false;
        }
        for (char c : who.toCharArray()) {
            if (!isNummeric(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isIntegeric(String who) {
        return who.length() < 10 && isNummeric(who);
    }

    public static boolean between(int who, int a, int b) {
        return who >= a && who <= b;
    }

    public static String lc(String text) {
        return text == null ? null : text.toLowerCase();
    }

    /**
     * transforming string to the corrosponding enum 
     * @param <T>
     * @param what
     * @param cls
     * @return 
     */
    public static <T extends Enum> T enumarate(String what, Class<T> cls) {
        EnumSet es = EnumSet.allOf(cls);
        for (Object e : es) {
            if (eq(((Enum) e).name(), what)) {
                return (T) e;
            }
        }

        return null;
    }

    public static <T> List<T> list(T... data) {
        ArrayList al = new ArrayList(data.length);
        for (T d : data) {
            al.add(d);
        }
        return al;
    }

    /**
     * n-ary min function
     * @param <T>
     * @param args
     * @return
     */
    public static <T extends Number> T min(T... args) {
        int min = 0;
        for (int i = 1; i < args.length; i++) {
            min = (args[min].doubleValue() > args[i].doubleValue() ? i : min);
        }

        return args[min];
    }

    /**
     * n-ary max function
     * @param <T>
     * @param args
     * @return
     */
    public static <T extends Number> T max(T... args) {
        int min = 0;
        for (int i = 1; i < args.length; i++) {
            min = (args[min].doubleValue() < args[i].doubleValue() ? i : min);
        }

        return args[min];
    }
}
