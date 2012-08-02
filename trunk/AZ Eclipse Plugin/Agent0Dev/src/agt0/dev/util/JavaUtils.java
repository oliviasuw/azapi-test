package agt0.dev.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.internal.quickaccess.CamelUtil;

import static agt0.dev.util.SourceUtils.*;

public class JavaUtils {

	private static final Logger log = Logger.getLogger(JavaUtils.class
			.getName());

	public static <T> T[] concat(T[] arr, T... ts) {
		T[] ret = Arrays.copyOf(arr, arr.length + ts.length);
		System.arraycopy(ts, 0, ret, arr.length, ts.length);
		return ret;
	}

	public static <T> List<T> list(T[] array) {
		ArrayList<T> ret = new ArrayList<T>(array.length);
		for (T t : array)
			ret.add(t);
		return ret;
	}

	public static char uc(char ch) {
		return Character.toUpperCase(ch);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> assoc(Map<K, V> with, Object... kv) {
		for (int i = 0; i < kv.length; i += 2) {
			with.put((K) kv[i], (V) kv[i + 1]);
		}

		return with;
	}

	public static <T> List<T> filter(List<T> what, Fn<Boolean> filter) {
		List<T> ret = new LinkedList<T>();
		for (T t : what) {
			if (filter.invoke(t))
				ret.add(t);
		}

		return ret;
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

	public static <F, T> List<T> map(F[] what, Fn1<F, T> mapper) {
		List<T> ret = new ArrayList<T>(what.length);
		for (F w : what)
			ret.add(mapper.invoke(w));
		return ret;
	}

	public static boolean eq(Object str1, Object str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}

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

	public static boolean eqor(String str1, String... ors) {
		for (String o : ors) {
			if (eq(str1, o)) {
				return true;
			}
		}
		return false;
	}

	public static boolean eqor(Object str1, Object... ors) {
		for (Object o : ors) {
			if (str1.equals(o)) {
				return true;
			}
		}
		return false;
	}
	
	public static <T> T select(T[] from, Fn1<T, Boolean> selector){
		for (T f : from){
			if (selector.invoke(f)) return f;
		}
		
		return null;
	}

	public static abstract class Fn1<GET, RET> implements Fn<RET> {
		public abstract RET invoke(GET arg);

		@SuppressWarnings("unchecked")
		@Override
		public RET invoke(Object... args) {
			return invoke((GET) args[0]);
		}
	}

	public static interface Fn<T> {
		public T invoke(Object... args);
	}

	public static void println(String what) {
		System.out.println(what);
	}

	public static void log(Exception ex, String desc) {
		log.log(Level.SEVERE, desc, ex);
	}

	public static void log(Exception ex) {
		log(ex, null);
	}

	public static void log(String what) {
		// log.info(what);
		System.out.println(what);
	}

	public static int min(int... m) {
		int ret = m[0];
		for (int i = 1; i < m.length; i++) {
			if (ret > m[i])
				ret = m[i];
		}

		return ret;
	}

	public static int max(int... m) {
		int ret = m[0];
		for (int i = 1; i < m.length; i++) {
			if (ret < m[i])
				ret = m[i];
		}

		return ret;
	}

	public static Integer cint(String i) {
		try {
			return Integer.parseInt(i);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	public static boolean isNummeric(String what){
		return isNummeric(what.toCharArray());
	}

	public static boolean isNummeric(char[] charArray) {
		for (char c : charArray){
			if (!isNummeric(c)) return false;
		}
		
		return true;
	}

	public static boolean isNummeric(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static String drop(String from, int sum){
		return from.substring(0, from.length()-sum-1);
	}
	
	
	public static Fn1<Object, Boolean> compareByFn(final String by ,final String what){
		return new Fn1<Object, Boolean>() {

			@Override
			public Boolean invoke(Object arg) {
				try {
					Object getterVal = arg.getClass().getMethod("get" + camelCase(by)).invoke(arg);
					println("getter value is " + getterVal);
					return getterVal.equals(what);
				} catch (Exception e) {
					return false;
				}
			}
			
		};
	}
	
	public static Fn1<Object, Void> printlnFn(){
		return new Fn1<Object, Void>() {

			@Override
			public Void invoke(Object arg) {
				println(arg.toString());
				return null;
			}
		};
	}
	
	public static void iter(Object[] arr, Fn1 fn){
		for (Object a : arr){
			fn.invoke(a);
		}
	}
}
