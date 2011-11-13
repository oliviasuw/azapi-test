/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Simulator Domain Specific Language Object is a group of functions that simplify some programming tasks
 * designed for the lazy programmer :)
 * 
 * under constraction!
 * @author bennyl
 */
public class Agt0DSL {

    private static final Pattern nummericPattern = Pattern.compile("[-+]?\\d+(\\.\\d*)?$");
    private static final Pattern integericPattern = Pattern.compile("[-+]?\\d+$");
    
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

    /**
     * return a string representation for the collection col.
     * @param col
     * @return
     */
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

    /**
     * if sum is possitive - return from without the first 'sum' chars
     * if is negative return only the last 'sum' chars of 'from'
     * @param from
     * @param sum
     * @return
     */
    public static String drop(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(sum);
    }

    /**
     * if sum is positive: return the first $sum letters from $from
     * if sum is negative: return all chars from $from but the last $sum
     * @param from
     * @param sum
     * @return
     */
    public static String take(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(0, sum);
    }

    /**
     * return true if the given char is nummeric
     * @param who
     * @return 
     */
    public static boolean isNummeric(char who) {
        return who >= '0' && who <= '9';
    }

    /**
     * return true if the given string is nummeric
     * @param who
     * @return 
     */
    public static boolean isNummeric(String who) {
        return nummericPattern.matcher(who).matches();
    }

    /**
     * @param who
     * @return true if the given string represents an integer number
     */
    public static boolean isIntegeric(String who) {
        return integericPattern.matcher(who).matches();
    }

    /**
     * @param who
     * @param a
     * @param b
     * @return true if $who is between $a and $b: $a <= $who <= $b.
     */
    public static boolean between(int who, int a, int b) {
        return who >= a && who <= b;
    }

    /**
     * @param text
     * @return the given string in lower case
     */
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

    /**
     * @param <T>
     * @param data
     * @return - the collection of the given objects grouped in a list.
     */
    public static <T> List<T> list(T... data) {
        return Arrays.asList(data);
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
    
    /**
     * trick to throw checked exception in uncheck context - do not use unless you know what you are doing
     * or you just dont care about this exception
     * @param e 
     */
    public static void throwUncheked(Throwable e) {
        Agt0DSL.<RuntimeException>throwAny(e);
    }
   
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(Throwable e) throws E {
        throw (E)e;
    }
    
    /**
     * select a random item from the given list
     * @param <T>
     * @param c
     * @return 
     */
    public static <T> T random(List<T> c ){
       Random r = new Random();
       if (c.isEmpty()) return null;
       return c.get(r.nextInt(c.size()));
    }
    
    /**
     * select a random item from the given set
     * @param <T>
     * @param c
     * @return 
     */
    public static <T> T random(Set<T> c ){
       Random r = new Random();
       return (T) random(c.toArray()); 
    }
    
    
    /**
     * select a random item from the given array
     * @param <T>
     * @param c
     * @return 
     */
    public static <T> T random(T[] c ){
       Random r = new Random();
       if (c.length == 0) return null;
       return c[r.nextInt(c.length)];
    }
}
