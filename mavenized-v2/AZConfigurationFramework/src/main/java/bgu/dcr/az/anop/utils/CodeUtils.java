package bgu.dcr.az.anop.utils;

/**
 *
 * @author User
 */
public class CodeUtils {
    /**
     * convert identifier names that look like: thisIsAnIdentifierName to this_is_an_identifier_name
     * @param value
     * @return 
     */
    public static String camelCaseToLowerLine(String value){
        if (value.isEmpty()) return value;
        
        char[] chars = value.toCharArray();
        StringBuilder sb = new StringBuilder("" + chars[0]);
        
        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])){
                sb.append("_");
            }
            
            sb.append(Character.toLowerCase(chars[i]));
        }
        
        return sb.toString();
    }
}
