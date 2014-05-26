package bgu.dcr.az.metagen.util;

import org.apache.commons.lang.StringEscapeUtils;

public class TemplateUtils {
	/**
	 * convert a camel case string to all caps string. e.g.,
	 * {@code camelCase -> CAMEL_CASE}
	 * 
	 * @param v
	 * @return
	 */
	public static String camel2allcaps(String v) {
		StringBuilder result = new StringBuilder();
		int lastCapSeen = 0;
		char[] chars = v.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (Character.isUpperCase(chars[i])) {
				if (lastCapSeen != i - 1) {
					result.append("_");
				}

				lastCapSeen = i;
			}

			result.append(Character.toUpperCase(chars[i]));
		}

		return result.toString();
	}

	/**
	 * return the given string escaped from all java special characters
	 * 
	 * @param v
	 * @return
	 */
	public static String escape(String v) {
		return StringEscapeUtils.escapeJava(v);
	}
}
